package com.example.demo.repository;

import com.example.demo.model.StaffAttendance;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {
    Optional<StaffAttendance> findByStaffUserAndAttendanceDate(User staffUser, LocalDate attendanceDate);
    List<StaffAttendance> findByStaffUserAndAttendanceDateBetweenOrderByAttendanceDateDesc(User staffUser, LocalDate from, LocalDate to);
}
