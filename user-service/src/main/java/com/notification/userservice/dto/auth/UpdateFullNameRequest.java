package com.notification.userservice.dto.auth;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record UpdateFullNameRequest(@NotBlank String fullName){}
