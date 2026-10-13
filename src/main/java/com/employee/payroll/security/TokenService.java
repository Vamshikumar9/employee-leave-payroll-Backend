package com.employee.payroll.security;

import com.employee.payroll.user.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private final SecretKey key;
    private final long lifetimeMinutes;

    public TokenService(@Value("${security.jwt.secret}") String secret,
                        @Value("${security.jwt.lifetime-minutes:30}") long lifetimeMinutes) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) throw new IllegalArgumentException("JWT_SECRET must contain at least 32 characters.");
        this.key = Keys.hmacShaKeyFor(bytes);
        this.lifetimeMinutes = lifetimeMinutes;
    }

    public String issue(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getEmail()).id(UUID.randomUUID().toString())
            .claim("role", user.getRole().name()).issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(lifetimeMinutes * 60)))
            .signWith(key).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
