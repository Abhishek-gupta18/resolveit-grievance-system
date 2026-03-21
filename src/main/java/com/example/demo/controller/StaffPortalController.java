package com.example.demo.controller;

import com.example.demo.dto.StaffAttendanceRequest;
import com.example.demo.dto.CommentRequest;
import com.example.demo.service.StaffPortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaffPortalController {

    @Autowired
    private StaffPortalService staffPortalService;

    @GetMapping("/api/staff/dashboard")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        try {
            return ResponseEntity.ok(staffPortalService.getStaffDashboard(authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load staff dashboard: " + e.getMessage());
        }
    }

    @GetMapping("/api/staff/complaints")
    public ResponseEntity<?> getAssignedComplaints(Authentication authentication) {
        try {
            return ResponseEntity.ok(staffPortalService.getMyAssignedComplaints(authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load assigned complaints: " + e.getMessage());
        }
    }

    @GetMapping("/api/staff/attendance")
    public ResponseEntity<?> getAttendance(
            Authentication authentication,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to
    ) {
        try {
            return ResponseEntity.ok(staffPortalService.getMyAttendance(authentication.getName(), from, to));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load attendance: " + e.getMessage());
        }
    }

    @PutMapping("/api/staff/complaints/{complaintId}/resolve")
    public ResponseEntity<?> markComplaintResolved(
            @PathVariable Long complaintId,
            @RequestBody(required = false) CommentRequest request,
            Authentication authentication
    ) {
        try {
            return ResponseEntity.ok(staffPortalService.updateAssignedComplaintStatus(
                    complaintId,
                    "resolved",
                    request != null ? request.getComment() : null,
                    authentication.getName()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark complaint as resolved: " + e.getMessage());
        }
    }

    @PutMapping("/api/staff/complaints/{complaintId}/escalate")
    public ResponseEntity<?> escalateComplaint(
            @PathVariable Long complaintId,
            @RequestBody(required = false) CommentRequest request,
            Authentication authentication
    ) {
        try {
            return ResponseEntity.ok(staffPortalService.updateAssignedComplaintStatus(
                    complaintId,
                    "escalated",
                    request != null ? request.getComment() : null,
                    authentication.getName()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to escalate complaint: " + e.getMessage());
        }
    }

    @PostMapping("/api/admin/attendance/mark")
    public ResponseEntity<?> markAttendance(
            @RequestBody StaffAttendanceRequest request,
            Authentication authentication
    ) {
        try {
            return ResponseEntity.ok(staffPortalService.markStaffAttendance(request, authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark attendance: " + e.getMessage());
        }
    }
}
