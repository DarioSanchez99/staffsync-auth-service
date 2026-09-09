package com.staffsync.auth.infrastructure.config;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.out.TokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtConfig implements TokenPort {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtConfig(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(User user) {
        String role = user.role() != null ? user.role().name() : "";
        String permissions = user.role() != null
                ? user.role().permissions().stream()
                        .map(Permission::name)
                        .collect(Collectors.joining(","))
                : "";

        return Jwts.builder()
                .subject(user.id().toString())
                .claim("role", role)
                .claim("permissions", permissions)
                .claim("email", user.email())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
