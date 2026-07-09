package com.notification.userservice.dto.contact;

public record UpdateContactRequest(String name,
                                   String email,
                                   String phone,
                                   String telegramId) {
}
