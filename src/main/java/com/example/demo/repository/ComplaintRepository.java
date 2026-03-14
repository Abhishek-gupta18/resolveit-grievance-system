package com.example.demo.repository;

import com.example.demo.model.Complaint;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserOrderByCreatedAtDesc(User user);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    List<Complaint> findByAssignedStaffIsNullOrderByCreatedAtAsc();
    List<Complaint> findByAssignedStaffOrderByCreatedAtDesc(User assignedStaff);
    List<Complaint> findByAssignedStaffAndStatusOrderByCreatedAtDesc(User assignedStaff, String status);
    List<Complaint> findByAssignedStaffAndStatusAndCategoryOrderByCreatedAtDesc(User assignedStaff, String status, String category);
        List<Complaint> findByAssignedStaffIsNotNullAndStatusInAndCreatedAtBeforeOrderByCreatedAtAsc(
            List<String> statuses,
            LocalDateTime createdAt
        );
    long countByAssignedStaffAndCreatedAtBetween(User assignedStaff, LocalDateTime start, LocalDateTime end);
    long countByAssignedStaffAndStatusAndResolvedAtBetween(User assignedStaff, String status, LocalDateTime start, LocalDateTime end);
    boolean existsByComplaintCode(String complaintCode);
}
