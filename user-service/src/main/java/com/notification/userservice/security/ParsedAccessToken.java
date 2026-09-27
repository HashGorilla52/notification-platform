package com.notification.userservice.security;

import java.util.Date;
import java.util.UUID;

public record ParsedAccessToken(UUID userId, String sub, String type, Date expiration) {
}
