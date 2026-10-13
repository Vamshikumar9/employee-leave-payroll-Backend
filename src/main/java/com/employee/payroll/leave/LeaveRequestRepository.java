package com.employee.payroll.leave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {
 List<LeaveRequest> findAllByOrderByCreatedAtDesc();
 List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
 void deleteByEmployeeId(Long employeeId);
}
