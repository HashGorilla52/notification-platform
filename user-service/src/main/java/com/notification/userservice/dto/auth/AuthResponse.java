package com.notification.userservice.dto.auth;

public record AuthResponse(String accessToken, String refreshToken, String email, String fullName) {}
