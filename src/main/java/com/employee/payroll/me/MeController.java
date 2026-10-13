package com.employee.payroll.me;

import com.employee.payroll.employee.Employee;
import com.employee.payroll.employee.EmployeeRepository;
import com.employee.payroll.user.AppUser;
import com.employee.payroll.user.AppUserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/me")
public class MeController {
    private final AppUserRepository users;
    private final EmployeeRepository employees;
    public MeController(AppUserRepository users, EmployeeRepository employees) { this.users = users; this.employees = employees; }

    @GetMapping
    public Employee myProfile(@AuthenticationPrincipal String email) {
        AppUser user = users.findByEmail(email).filter(AppUser::isActive)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (user.getRole().name().equals("ADMIN") || user.getEmployeeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No employee profile is linked to this account");
        }
        return employees.findById(user.getEmployeeId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee profile not found"));
    }
}
