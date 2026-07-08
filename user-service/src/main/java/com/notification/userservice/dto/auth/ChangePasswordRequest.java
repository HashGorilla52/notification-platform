package com.notification.userservice.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(@NotBlank
                                    @Size(min = 8, max = 32)
                                    String oldPassword,
                                    @NotBlank
                                    @Size(min = 8, max = 32)
                                    String newPassword) {}
