package com.employee.payroll.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapAdmin {
    @Bean CommandLineRunner createInitialAdmin(AppUserRepository users, PasswordEncoder encoder,
        @Value("${bootstrap.admin.email:}") String adminEmail,
        @Value("${bootstrap.admin.password:}") String adminPassword) {
        return args -> {
            if (users.count() == 0) {
                if (adminEmail.isBlank() || adminPassword.length() < 12) {
                    throw new IllegalStateException("No users exist. Set BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD (at least 12 characters) to create the first admin.");
                }
                AppUser admin = new AppUser();
                admin.setEmail(adminEmail);
                admin.setPasswordHash(encoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);
                admin.setActive(true);
                users.save(admin);
                System.out.println("Created bootstrap administrator: " + adminEmail);
            }
        };
    }
}
