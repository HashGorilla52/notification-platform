package com.notification.userservice.security;

import com.notification.userservice.exception.InvalidTokenException;
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
    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    public JwtCore(
            @Value("${jwt.secret}")  String key, @Value("${jwt.access-token-expiration}") long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshExpiration,
            @Value("${jwt.issuer}") String issuer
            ) {

        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessExpiration;
        this.refreshTokenExpiration = refreshExpiration;
        this.issuer = issuer;
    }

    public String generateAccessToken(UUID userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", ACCESS);
        return buildToken(email, claims, accessTokenExpiration);
    }

    public String generateRefreshToken(UUID userId, String email, long version) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", REFRESH);
        claims.put("version", version);
        return buildToken(email, claims, refreshTokenExpiration);
    }

    public ParsedRefreshToken parseRefreshToken(String token) {
        Claims claims = parseToken(token).getPayload();
        if (!REFRESH.equals(claims.get("type"))){
            throw new InvalidTokenException("Invalid token");
        }
        return new ParsedRefreshToken(
                UUID.fromString(claims.get("userId", String.class)),
                claims.getSubject(),
                claims.get("type", String.class),
                claims.get("version", Long.class),
                claims.getExpiration()
        );
    }

    public ParsedAccessToken parseAccessToken(String token) {
        Claims claims = parseToken(token).getPayload();
        if (!ACCESS.equals(claims.get("type"))){
            throw new InvalidTokenException("Invalid token");
        }
        return new ParsedAccessToken(
                UUID.fromString(claims.get("userId", String.class)),
                claims.getSubject(),
                claims.get("type", String.class),
                claims.getExpiration()
        );
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