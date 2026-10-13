package com.employee.payroll.auth;

import com.employee.payroll.user.AppUser;

public record LoginResponse(String token, UserView user) {
    public record UserView(Long id, String email, String role, Long employeeId) {
        public static UserView from(AppUser user) { return new UserView(user.getId(), user.getEmail(), user.getRole().name(), user.getEmployeeId()); }
    }
}
