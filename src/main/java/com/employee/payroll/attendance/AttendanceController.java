package com.employee.payroll.attendance;
import com.employee.payroll.employee.Employee; import com.employee.payroll.employee.EmployeeRepository; import com.employee.payroll.user.*;
import org.springframework.http.HttpStatus; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
import java.time.*; import java.util.*;
@RestController @RequestMapping("/api/me/attendance")
public class AttendanceController {
 private final AttendanceRepository attendance; private final AppUserRepository users; private final EmployeeRepository employees;
 public AttendanceController(AttendanceRepository a,AppUserRepository u,EmployeeRepository e){attendance=a;users=u;employees=e;}
 private Employee employee(String email){AppUser u=users.findByEmail(email).filter(AppUser::isActive).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));if(u.getEmployeeId()==null)throw new ResponseStatusException(HttpStatus.FORBIDDEN);return employees.findById(u.getEmployeeId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 @GetMapping public List<AttendanceRecord> history(@AuthenticationPrincipal String email){return attendance.findByEmployeeIdOrderByAttendanceDateDesc(employee(email).getId());}
 @PostMapping("/check-in") public AttendanceRecord checkIn(@AuthenticationPrincipal String email){Employee e=employee(email);LocalDate today=LocalDate.now();AttendanceRecord r=attendance.findByEmployeeIdAndAttendanceDate(e.getId(),today).orElseGet(()->{AttendanceRecord n=new AttendanceRecord();n.setEmployeeId(e.getId());n.setEmployeeName(e.getName());n.setAttendanceDate(today);return n;});if(r.getCheckIn()!=null)throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already checked in today");r.setCheckIn(LocalTime.now().withNano(0));r.setStatus("Present");return attendance.save(r);}
 @PostMapping("/check-out") public AttendanceRecord checkOut(@AuthenticationPrincipal String email){Employee e=employee(email);AttendanceRecord r=attendance.findByEmployeeIdAndAttendanceDate(e.getId(),LocalDate.now()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Check in before checking out"));if(r.getCheckIn()==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Check in before checking out");if(r.getCheckOut()!=null)throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already checked out today");r.setCheckOut(LocalTime.now().withNano(0));return attendance.save(r);}
}
