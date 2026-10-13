package com.employee.payroll.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateEmployeeRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotBlank String department,
    @NotBlank String role,
    @NotNull @PositiveOrZero BigDecimal salary,
    @NotBlank @Size(min = 12, max = 72) String password
) {}
