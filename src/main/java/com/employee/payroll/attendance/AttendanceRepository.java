package com.employee.payroll.attendance;
import org.springframework.data.jpa.repository.JpaRepository; import java.time.LocalDate; import java.util.*;
public interface AttendanceRepository extends JpaRepository<AttendanceRecord,Long>{
 List<AttendanceRecord> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);
 Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(Long employeeId,LocalDate date);
 void deleteByEmployeeId(Long employeeId);
}
