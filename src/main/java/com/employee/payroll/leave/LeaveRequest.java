package com.employee.payroll.leave;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private Long employeeId;
    @Column(nullable=false) private String employeeName;
    @Column(nullable=false) private String employeeEmail;
    @Column(nullable=false) private String leaveType;
    @Column(nullable=false) private LocalDate startDate;
    @Column(nullable=false) private LocalDate endDate;
    @Column(nullable=false, length=1000) private String reason;
    @Column(nullable=false) private String status = "Pending";
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    public LeaveRequest() {}
    public Long getId(){return id;} public Long getEmployeeId(){return employeeId;} public void setEmployeeId(Long v){employeeId=v;}
    public String getEmployeeName(){return employeeName;} public void setEmployeeName(String v){employeeName=v;}
    public String getEmployeeEmail(){return employeeEmail;} public void setEmployeeEmail(String v){employeeEmail=v;}
    public String getLeaveType(){return leaveType;} public void setLeaveType(String v){leaveType=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;}
    public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
    public String getReason(){return reason;} public void setReason(String v){reason=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Instant getCreatedAt(){return createdAt;}
    public long getDays(){return java.time.temporal.ChronoUnit.DAYS.between(startDate,endDate)+1;}
}
