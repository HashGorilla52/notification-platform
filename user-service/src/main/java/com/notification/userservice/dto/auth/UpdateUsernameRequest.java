package com.notification.userservice.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameRequest(@NotBlank String username){}
