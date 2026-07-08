package com.notification.userservice.controller.auth;

import com.notification.userservice.dto.auth.*;
import com.notification.userservice.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestHeader(name = "Authorization") String authHeader) {
        String refreshToken = authHeader.substring("Bearer ".length());
        return authService.refresh(refreshToken);
    }

    @PostMapping("/change-password")
    public AuthResponse changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authService.changePassword(email, changePasswordRequest);
    }

    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFullName(@Valid @RequestBody UpdateFullNameRequest updateFullNameRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.updateFullName(email, updateFullNameRequest);
    }

    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public void validate() {

    }

}
