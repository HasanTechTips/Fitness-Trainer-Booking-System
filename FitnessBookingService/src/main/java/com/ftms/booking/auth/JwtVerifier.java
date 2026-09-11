package com.ftms.booking.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class JwtVerifier {
    private static final String DEFAULT_SECRET = "change-me-jwt-secret";

    private JwtVerifier() {
    }

    public static Claims verifyAndGetClaims(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing bearer token.");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getSigningKey() {
        String secret = System.getenv("FTMS_JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = DEFAULT_SECRET;
        }
        byte[] keyBytes = String.format("%-32s", secret).substring(0, 32)
                .getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
