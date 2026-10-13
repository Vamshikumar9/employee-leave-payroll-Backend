package com.employee.payroll.leave;

import com.employee.payroll.employee.Employee;
import com.employee.payroll.employee.EmployeeRepository;
import com.employee.payroll.user.AppUser;
import com.employee.payroll.user.AppUserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
public class LeaveController {
 private final LeaveRequestRepository leaves; private final AppUserRepository users; private final EmployeeRepository employees;
 public LeaveController(LeaveRequestRepository leaves, AppUserRepository users, EmployeeRepository employees){this.leaves=leaves;this.users=users;this.employees=employees;}
 public record ApplyLeaveRequest(@NotBlank String leaveType,@NotNull LocalDate startDate,@NotNull LocalDate endDate,@NotBlank String reason){}
 public record ReviewRequest(@NotBlank String status){}
 private AppUser current(String email){return users.findByEmail(email).filter(AppUser::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
 @GetMapping("/api/me/leaves") public List<LeaveRequest> myLeaves(@AuthenticationPrincipal String email){AppUser u=current(email); if(u.getEmployeeId()==null) throw new ResponseStatusException(HttpStatus.FORBIDDEN); return leaves.findByEmployeeIdOrderByCreatedAtDesc(u.getEmployeeId());}
 @PostMapping("/api/me/leaves") @ResponseStatus(HttpStatus.CREATED) public LeaveRequest apply(@AuthenticationPrincipal String email,@Valid @RequestBody ApplyLeaveRequest input){
  AppUser u=current(email); if(u.getEmployeeId()==null) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  if(input.endDate().isBefore(input.startDate())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"End date cannot be before start date");
  if(input.startDate().isBefore(LocalDate.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Leave start date cannot be in the past");
  Employee e=employees.findById(u.getEmployeeId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  LeaveRequest l=new LeaveRequest();l.setEmployeeId(e.getId());l.setEmployeeName(e.getName());l.setEmployeeEmail(e.getEmail());l.setLeaveType(input.leaveType());l.setStartDate(input.startDate());l.setEndDate(input.endDate());l.setReason(input.reason().trim());l.setStatus("Pending");return leaves.save(l);
 }
 @GetMapping("/api/leaves") public List<LeaveRequest> allLeaves(){return leaves.findAllByOrderByCreatedAtDesc();}
 @PatchMapping("/api/leaves/{id}") public LeaveRequest review(@PathVariable Long id,@Valid @RequestBody ReviewRequest input){
  String status=input.status().trim(); if(!List.of("Approved","Rejected").contains(status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Status must be Approved or Rejected");
  LeaveRequest l=leaves.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Leave request not found"));l.setStatus(status);return leaves.save(l);
 }
}
