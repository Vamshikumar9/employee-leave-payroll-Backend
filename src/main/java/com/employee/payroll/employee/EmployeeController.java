package com.employee.payroll.employee;

import com.employee.payroll.user.AppUser;
import com.employee.payroll.user.AppUserRepository;
import com.employee.payroll.user.UserRole;
import com.employee.payroll.leave.LeaveRequestRepository;
import com.employee.payroll.attendance.AttendanceRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository repository;
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final LeaveRequestRepository leaveRequests;
    private final AttendanceRepository attendance;

    public EmployeeController(EmployeeRepository repository, AppUserRepository users, PasswordEncoder passwordEncoder, LeaveRequestRepository leaveRequests, AttendanceRepository attendance) {
        this.repository = repository; this.users = users; this.passwordEncoder = passwordEncoder; this.leaveRequests = leaveRequests; this.attendance = attendance;
    }

    @GetMapping public List<Employee> list() { return repository.findAll(); }

    @GetMapping("/{id}") public Employee get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    @PostMapping @Transactional
    public ResponseEntity<Employee> create(@Valid @RequestBody CreateEmployeeRequest input) {
        String email = input.email().trim().toLowerCase();
        if (repository.existsByEmail(email) || users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        Employee employee = new Employee();
        employee.setName(input.name().trim()); employee.setEmail(email); employee.setDepartment(input.department().trim());
        employee.setRole(input.role().trim()); employee.setSalary(input.salary()); employee.setStatus("Active");
        Employee saved = repository.save(employee);
        AppUser user = new AppUser(); user.setEmail(email); user.setPasswordHash(passwordEncoder.encode(input.password()));
        user.setRole(UserRole.EMPLOYEE); user.setEmployeeId(saved.getId()); user.setActive(true); users.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}") @Transactional
    public Employee update(@PathVariable Long id, @Valid @RequestBody Employee input) {
        Employee employee = get(id);
        String email = input.getEmail().trim().toLowerCase();
        if (!employee.getEmail().equalsIgnoreCase(email) && (repository.existsByEmail(email) || users.existsByEmail(email))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        String oldEmail = employee.getEmail();
        employee.setName(input.getName()); employee.setEmail(email); employee.setDepartment(input.getDepartment());
        employee.setRole(input.getRole()); employee.setSalary(input.getSalary());
        employee.setStatus(input.getStatus() == null ? employee.getStatus() : input.getStatus());
        Employee saved = repository.save(employee);
        users.findByEmail(oldEmail.toLowerCase()).ifPresent(user -> { user.setEmail(email); users.save(user); });
        return saved;
    }

    @DeleteMapping("/{id}") @Transactional
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        Employee employee = get(id);
        leaveRequests.deleteByEmployeeId(id);
        attendance.deleteByEmployeeId(id);
        users.findByEmail(employee.getEmail().toLowerCase()).ifPresent(users::delete);
        repository.delete(employee);
        return ResponseEntity.noContent().build();
    }
}
