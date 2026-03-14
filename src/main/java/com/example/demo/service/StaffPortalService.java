package com.example.demo.service;

import com.example.demo.dto.StaffAssignedComplaintResponse;
import com.example.demo.dto.StaffAttendanceRequest;
import com.example.demo.dto.StaffAttendanceResponse;
import com.example.demo.dto.StaffDashboardResponse;
import com.example.demo.model.Complaint;
import com.example.demo.model.StaffAttendance;
import com.example.demo.model.User;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.StaffAttendanceRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class StaffPortalService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String SENIOR_HANDLER_RANK = "Senior Handler";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private StaffAttendanceRepository staffAttendanceRepository;

    @Autowired
    private ComplaintService complaintService;

    public StaffDashboardResponse getStaffDashboard(String authenticatedEmail) {
        User staffUser = getStaffUser(authenticatedEmail);
        List<Complaint> scopedComplaints = getScopedComplaintsForStaff(staffUser);

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();

        LocalDate monthStartDate = today.withDayOfMonth(1);
        LocalDateTime monthStart = monthStartDate.atStartOfDay();
        LocalDateTime monthEnd = today.plusDays(1).atStartOfDay();

        long todayAssignedCount = scopedComplaints.stream()
                .filter(complaint -> complaint.getCreatedAt() != null
                        && !complaint.getCreatedAt().isBefore(todayStart)
                        && complaint.getCreatedAt().isBefore(todayEnd))
                .count();

        long completedTodayCount = scopedComplaints.stream()
                .filter(complaint -> "resolved".equals(normalizeComplaintActionStatus(complaint.getStatus())))
                .filter(complaint -> complaint.getResolvedAt() != null
                        && !complaint.getResolvedAt().isBefore(todayStart)
                        && complaint.getResolvedAt().isBefore(todayEnd))
                .count();

        long completedMonthCount = scopedComplaints.stream()
                .filter(complaint -> "resolved".equals(normalizeComplaintActionStatus(complaint.getStatus())))
                .filter(complaint -> complaint.getResolvedAt() != null
                        && !complaint.getResolvedAt().isBefore(monthStart)
                        && complaint.getResolvedAt().isBefore(monthEnd))
                .count();

        List<StaffAssignedComplaintResponse> assignedComplaints = scopedComplaints.stream()
                .map(this::toAssignedComplaint)
                .toList();

        StaffAttendanceResponse todayAttendance = staffAttendanceRepository.findByStaffUserAndAttendanceDate(staffUser, today)
                .map(this::toAttendanceResponse)
                .orElse(null);

        List<StaffAttendanceResponse> recentAttendance = staffAttendanceRepository
                .findByStaffUserAndAttendanceDateBetweenOrderByAttendanceDateDesc(staffUser, today.minusDays(30), today)
                .stream()
                .map(this::toAttendanceResponse)
                .toList();

        return new StaffDashboardResponse(
                staffUser.getStaffId(),
                staffUser.getName(),
                staffUser.getEmail(),
                staffUser.getRank(),
                staffUser.getSpecializationCategory(),
                todayAssignedCount,
                completedTodayCount,
                completedMonthCount,
                todayAttendance,
                assignedComplaints,
                recentAttendance
        );
    }

    public List<StaffAssignedComplaintResponse> getMyAssignedComplaints(String authenticatedEmail) {
        User staffUser = getStaffUser(authenticatedEmail);
        return getScopedComplaintsForStaff(staffUser)
                .stream()
                .map(this::toAssignedComplaint)
                .toList();
    }

    public List<StaffAttendanceResponse> getMyAttendance(String authenticatedEmail, String from, String to) {
        User staffUser = getStaffUser(authenticatedEmail);

        LocalDate fromDate = (from == null || from.isBlank()) ? LocalDate.now().minusDays(30) : LocalDate.parse(from);
        LocalDate toDate = (to == null || to.isBlank()) ? LocalDate.now() : LocalDate.parse(to);

        if (toDate.isBefore(fromDate)) {
            throw new RuntimeException("To date cannot be before from date");
        }

        return staffAttendanceRepository.findByStaffUserAndAttendanceDateBetweenOrderByAttendanceDateDesc(staffUser, fromDate, toDate)
                .stream()
                .map(this::toAttendanceResponse)
                .toList();
    }

    public StaffAssignedComplaintResponse updateAssignedComplaintStatus(
            Long complaintId,
            String targetStatus,
            String comment,
            String authenticatedEmail
    ) {
        User staffUser = getStaffUser(authenticatedEmail);

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getAssignedStaff() == null || !complaint.getAssignedStaff().getId().equals(staffUser.getId())) {
            throw new RuntimeException("You can only update complaints assigned to you");
        }

        String normalizedStatus = normalizeComplaintActionStatus(targetStatus);
        if (!Set.of("resolved", "escalated").contains(normalizedStatus)) {
            throw new RuntimeException("Invalid staff action. Allowed: resolved, escalated");
        }

        if ("escalated".equals(normalizedStatus) && isSeniorHandler(staffUser)) {
            throw new RuntimeException("Senior Handlers cannot escalate further. Please resolve or ask admin.");
        }

        String note = (comment == null || comment.isBlank())
                ? ("resolved".equals(normalizedStatus) ? "Resolved by staff" : "Escalated by staff")
                : comment.trim();

        complaintService.updateComplaintStatus(
                complaintId,
                normalizedStatus,
                note,
                null,
                authenticatedEmail
        );

        Complaint updatedComplaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found after update"));

        return toAssignedComplaint(updatedComplaint);
    }

    public StaffAttendanceResponse markStaffAttendance(StaffAttendanceRequest request, String authenticatedEmail) {
        User adminUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        String role = adminUser.getRole() == null ? "USER" : adminUser.getRole().toUpperCase(Locale.ENGLISH);
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied. Admin role required.");
        }

        if (request.getStaffUserId() == null) {
            throw new RuntimeException("Staff user id is required");
        }

        User staffUser = userRepository.findById(request.getStaffUserId())
                .orElseThrow(() -> new RuntimeException("Staff user not found"));

        String staffRole = staffUser.getRole() == null ? "USER" : staffUser.getRole().toUpperCase(Locale.ENGLISH);
        if (!"STAFF".equals(staffRole)) {
            throw new RuntimeException("Attendance can only be marked for STAFF users");
        }

        LocalDate attendanceDate = (request.getAttendanceDate() == null || request.getAttendanceDate().isBlank())
                ? LocalDate.now()
                : LocalDate.parse(request.getAttendanceDate());

        String status = normalizeAttendanceStatus(request.getStatus());

        StaffAttendance attendance = staffAttendanceRepository
                .findByStaffUserAndAttendanceDate(staffUser, attendanceDate)
                .orElseGet(StaffAttendance::new);

        attendance.setStaffUser(staffUser);
        attendance.setMarkedByUser(adminUser);
        attendance.setAttendanceDate(attendanceDate);
        attendance.setStatus(status);
        attendance.setCheckInTime(parseTime(request.getCheckInTime()));
        attendance.setCheckOutTime(parseTime(request.getCheckOutTime()));
        attendance.setNotes(request.getNotes());

        return toAttendanceResponse(staffAttendanceRepository.save(attendance));
    }

    private String normalizeAttendanceStatus(String status) {
        if (status == null || status.isBlank()) {
            return "present";
        }
        String normalized = status.trim().toLowerCase(Locale.ENGLISH);
        if (!List.of("present", "absent", "on_leave").contains(normalized)) {
            throw new RuntimeException("Invalid attendance status. Allowed: present, absent, on_leave");
        }
        return normalized;
    }

    private LocalTime parseTime(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        return LocalTime.parse(input);
    }

    private String normalizeComplaintActionStatus(String status) {
        if (status == null) {
            return "";
        }
        String normalized = status.trim().toLowerCase(Locale.ENGLISH);
        if ("in-progress".equals(normalized) || "in progress".equals(normalized)) {
            return "inprogress";
        }
        if ("done".equals(normalized) || "closed".equals(normalized)) {
            return "resolved";
        }
        return normalized;
    }

    private User getStaffUser(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        String role = user.getRole() == null ? "USER" : user.getRole().toUpperCase(Locale.ENGLISH);
        if (!"STAFF".equals(role)) {
            throw new RuntimeException("Access denied. Staff role required.");
        }

        return user;
    }

    private boolean isSeniorHandler(User user) {
        if (user == null || user.getRank() == null) {
            return false;
        }
        return SENIOR_HANDLER_RANK.equalsIgnoreCase(user.getRank().trim());
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "it-support-request";
        }

        String normalized = category.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.contains("hostel") || normalized.contains("academic") || normalized.contains("infrastructure")) {
            return "it-support-request";
        }

        return normalized;
    }

    private List<Complaint> getScopedComplaintsForStaff(User staffUser) {
        if (isSeniorHandler(staffUser)) {
            String specialization = normalizeCategory(staffUser.getSpecializationCategory());
            return complaintRepository.findByAssignedStaffAndStatusAndCategoryOrderByCreatedAtDesc(
                    staffUser,
                    "escalated",
                    specialization
            );
        }

        return complaintRepository.findByAssignedStaffOrderByCreatedAtDesc(staffUser);
    }

    private StaffAssignedComplaintResponse toAssignedComplaint(Complaint complaint) {
        return new StaffAssignedComplaintResponse(
                complaint.getId(),
                complaint.getComplaintCode(),
                complaint.getCategory(),
                complaint.getDescription(),
                complaint.getUrgency(),
                complaint.getStatus(),
                complaint.getUser() != null ? complaint.getUser().getName() : null,
                complaint.getCreatedAt() != null ? complaint.getCreatedAt().format(DATE_TIME_FORMATTER) : null,
                complaint.getUpdatedAt() != null ? complaint.getUpdatedAt().format(DATE_TIME_FORMATTER) : null
        );
    }

    private StaffAttendanceResponse toAttendanceResponse(StaffAttendance attendance) {
        return new StaffAttendanceResponse(
                attendance.getId(),
                attendance.getAttendanceDate() != null ? attendance.getAttendanceDate().toString() : null,
                attendance.getStatus(),
                attendance.getCheckInTime() != null ? attendance.getCheckInTime().format(TIME_FORMATTER) : null,
                attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().format(TIME_FORMATTER) : null,
                attendance.getNotes(),
                attendance.getMarkedByUser() != null ? attendance.getMarkedByUser().getName() : null
        );
    }
}
