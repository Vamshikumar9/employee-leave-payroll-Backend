package com.employee.payroll.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "revoked_tokens", indexes = @Index(name = "idx_revoked_token_jti", columnList = "jti", unique = true))
public class RevokedToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 80)
    private String jti;
    @Column(nullable = false)
    private Instant expiresAt;
    protected RevokedToken() {}
    public RevokedToken(String jti, Instant expiresAt) { this.jti = jti; this.expiresAt = expiresAt; }
    public String getJti() { return jti; }
    public Instant getExpiresAt() { return expiresAt; }
}
