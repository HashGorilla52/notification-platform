package com.notification.userservice.security;

import java.util.Date;
import java.util.UUID;

public record ParsedRefreshToken(UUID userId, String sub, String type, Long version, Date expiration) {
}
