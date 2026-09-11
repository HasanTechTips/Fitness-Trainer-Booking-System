package com.ftms.frontend.auth;

import com.ftms.frontend.helper.DebugTrace;
import com.ftms.frontend.helper.MemberInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String DEFAULT_SECRET = "change-me-jwt-secret";
    private static final long TOKEN_LIFETIME_MS = 30L * 60L * 1000L;

    private JwtUtil() {
    }

    public static String generateToken(MemberInfo member) {
        return generateToken(member, "jwt");
    }

    public static String generateToken(MemberInfo member, String traceId) {
        long now = System.currentTimeMillis();
        long start = DebugTrace.begin(traceId, "JWT generation");

        String token = Jwts.builder()
                .claim("memberId", member.getMemberID())
                .claim("username", member.getUsername())
                .claim("email", member.getEmail())
                .setSubject(member.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + TOKEN_LIFETIME_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        DebugTrace.mark(traceId, "JWT generation", start);
        return token;
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
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
