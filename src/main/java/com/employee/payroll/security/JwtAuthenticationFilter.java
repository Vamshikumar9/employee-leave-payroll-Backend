package com.employee.payroll.security;

import com.employee.payroll.user.AppUser;
import com.employee.payroll.user.AppUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokens;
    private final RevokedTokenRepository revokedTokens;
    private final AppUserRepository users;

    public JwtAuthenticationFilter(TokenService tokens, RevokedTokenRepository revokedTokens, AppUserRepository users) {
        this.tokens = tokens; this.revokedTokens = revokedTokens; this.users = users;
    }

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = tokens.parse(header.substring(7));
                String email = claims.getSubject();
                String jti = claims.getId();
                if (jti != null && !revokedTokens.existsByJti(jti)) {
                    AppUser user = users.findByEmail(email).filter(AppUser::isActive).orElse(null);
                    if (user != null && user.getRole().name().equals(claims.get("role", String.class))) {
                        var auth = new UsernamePasswordAuthenticationToken(email, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
