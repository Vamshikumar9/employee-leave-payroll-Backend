package com.employee.payroll.auth;

import com.employee.payroll.security.RevokedToken;
import com.employee.payroll.security.RevokedTokenRepository;
import com.employee.payroll.security.TokenService;
import com.employee.payroll.user.AppUser;
import com.employee.payroll.user.AppUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserRepository users;
    private final PasswordEncoder passwords;
    private final TokenService tokens;
    private final RevokedTokenRepository revokedTokens;

    public AuthController(AppUserRepository users, PasswordEncoder passwords, TokenService tokens, RevokedTokenRepository revokedTokens) {
        this.users = users; this.passwords = passwords; this.tokens = tokens; this.revokedTokens = revokedTokens;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = users.findByEmail(request.email().trim().toLowerCase())
            .filter(AppUser::isActive)
            .filter(u -> passwords.matches(request.password(), u.getPasswordHash()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return new LoginResponse(tokens.issue(user), LoginResponse.UserView.from(user));
    }

    @GetMapping("/me")
    public LoginResponse.UserView current(@org.springframework.security.core.annotation.AuthenticationPrincipal String email) {
        AppUser user = users.findByEmail(email).filter(AppUser::isActive)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account unavailable"));
        return LoginResponse.UserView.from(user);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return;
        try {
            Claims claims = tokens.parse(header.substring(7));
            String jti = claims.getId();
            if (jti != null && !revokedTokens.existsByJti(jti)) {
                Date expiry = claims.getExpiration();
                revokedTokens.save(new RevokedToken(jti, expiry.toInstant()));
            }
        } catch (RuntimeException ignored) { /* An expired or invalid token is already unusable. */ }
    }
}
