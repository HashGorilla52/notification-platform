package com.notification.userservice.dto.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateContactRequest(@NotBlank
                                   @Size(min=3, max=32)
                                   String name,
                                   @Email
                                   @NotBlank
                                   String email,
                                   String phone,
                                   String telegramId
                                   ) {
}
