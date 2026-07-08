package com.notification.userservice.service.auth;

import com.notification.userservice.dto.auth.*;
import com.notification.userservice.entity.User;
import com.notification.userservice.exception.ResourceAlreadyExistsException;
import com.notification.userservice.exception.UserNotFoundException;
import com.notification.userservice.repository.UserRepository;
import com.notification.userservice.security.JwtCore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtCore jwtCore;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ResourceAlreadyExistsException("User with email " + registerRequest.email() + " already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setFullName(registerRequest.fullName());

        User savedUser = userRepository.save(user);
        UUID id = savedUser.getId();
        String email = savedUser.getEmail();
        String fullName = savedUser.getFullName();
        long version = savedUser.getVersion();

        String accessToken = jwtCore.generateAccessToken(id, email);
        String refreshToken = jwtCore.generateRefreshToken(id, email, version);

        return new AuthResponse(accessToken, refreshToken, email, fullName);
    }

    public AuthResponse login(LoginRequest request) {
        String requestEmail = request.email();
        User user = userRepository.findByEmail(requestEmail).orElseThrow(
                () -> new UserNotFoundException("User with email " + requestEmail + " not found"));;

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        UUID userId = user.getId();
        String email = user.getEmail();
        String fullName = user.getFullName();
        long version = user.getVersion();

        String accessToken = jwtCore.generateAccessToken(userId, email);
        String refreshToken = jwtCore.generateRefreshToken(userId, email, version);

        return new AuthResponse(accessToken, refreshToken, email, fullName);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtCore.isValid(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String type = jwtCore.getTypeFromToken(refreshToken);
        if (!type.equals(JwtCore.REFRESH)) {
            throw new BadCredentialsException("Token is not a refresh token");
        }

        String email = jwtCore.getEmailFromToken(refreshToken);
        long requestVersion = jwtCore.getVersionFromToken(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email " + email + " not found")
        );

        long trueVersion = user.getVersion();
        if (requestVersion != trueVersion) {
            throw new BadCredentialsException("Token has been revoked");
        }

        String newRefreshToken = jwtCore.generateRefreshToken(user.getId(), email, trueVersion);
        String newAccessToken = jwtCore.generateAccessToken(user.getId(), email);

        return new AuthResponse(newAccessToken, newRefreshToken, email, user.getFullName());
    }

    public AuthResponse changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email " + email + " not found")
        );

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect old password");
        }

        long trueVersion = user.getVersion();
        long newVersion = trueVersion == Long.MAX_VALUE ? 0L : trueVersion + 1; // :)
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setVersion(newVersion);
        userRepository.save(user);
        String accessToken = jwtCore.generateAccessToken(user.getId(), email);
        String refreshToken = jwtCore.generateRefreshToken(user.getId(), email, newVersion);
        return new AuthResponse(accessToken, refreshToken, email, user.getFullName());
    }

    public void updateFullName(String email, UpdateFullNameRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setFullName(request.fullName());
        userRepository.save(user);
    }
}
