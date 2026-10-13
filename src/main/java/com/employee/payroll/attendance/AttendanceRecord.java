package com.employee.payroll.attendance;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="attendance_records", uniqueConstraints=@UniqueConstraint(name="uk_attendance_employee_date",columnNames={"employee_id","attendance_date"}))
public class AttendanceRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="employee_id",nullable=false) private Long employeeId;
 @Column(nullable=false) private String employeeName;
 @Column(name="attendance_date",nullable=false) private LocalDate attendanceDate;
 private LocalTime checkIn; private LocalTime checkOut;
 @Column(nullable=false) private String status="Present";
 public AttendanceRecord(){} public Long getId(){return id;} public Long getEmployeeId(){return employeeId;} public void setEmployeeId(Long v){employeeId=v;}
 public String getEmployeeName(){return employeeName;} public void setEmployeeName(String v){employeeName=v;}
 public LocalDate getAttendanceDate(){return attendanceDate;} public void setAttendanceDate(LocalDate v){attendanceDate=v;}
 public LocalTime getCheckIn(){return checkIn;} public void setCheckIn(LocalTime v){checkIn=v;}
 public LocalTime getCheckOut(){return checkOut;} public void setCheckOut(LocalTime v){checkOut=v;}
 public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
