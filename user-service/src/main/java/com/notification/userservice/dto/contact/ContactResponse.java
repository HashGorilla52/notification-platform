package com.notification.userservice.dto.contact;

import java.util.UUID;

public record ContactResponse(UUID id, UUID ownerId, String name, String email, String phone, String telegramId) {}
