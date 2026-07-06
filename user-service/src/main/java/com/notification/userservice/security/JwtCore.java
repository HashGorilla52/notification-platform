package com.notification.userservice.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generates and validates JWT access and refresh tokens.
 * Uses HMAC-SHA256 for signing.
 */
@Component
public class JwtCore {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;

    public JwtCore(
            @Value("${jwt.secret}")  String secret, @Value("${jwt.access-token-expiration}") long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshExpiration,
            @Value("${jwt.issuer}") String issuer
            ) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessExpiration;
        this.refreshTokenExpiration = refreshExpiration;
        this.issuer = issuer;
    }

    public String generateAccessToken(UUID userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "access");
        return buildToken(email, claims, accessTokenExpiration);
    }

    public String generateRefreshToken(UUID userId, String email, long version) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        claims.put("version", version);
        return buildToken(email, claims, refreshTokenExpiration);
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    public UUID getUserIdFromToken(String token) {
        return parseToken(token).getPayload().get("userId", UUID.class);
    }

    public Long getVersionFromToken(String token) {
        return parseToken(token).getPayload().get("version", Long.class);
    }

    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseToken(String token) {
       return Jwts.parser().verifyWith(key)
                .build().parseSignedClaims(token);
    }

    private String buildToken(String sub, Map<String, Object> claims, long expiration) {
        Instant now = Instant.now();
        JwtBuilder builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(sub)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiration)));

        claims.forEach(builder::claim);
        return builder.signWith(key).compact();
    }
}