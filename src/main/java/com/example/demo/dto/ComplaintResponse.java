package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ComplaintResponse {
    private Long id;
    private String complaintCode;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long assignedStaffId;
    private String assignedStaffName;
    private String assignedStaffEmail;
    private Boolean isAnonymous;
    private String category;
    private String description;
    private String urgency;
    private String status;
    private String adminReview;
    private String proofFileName;
    private String proofFileType;
    private String proofFileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private List<StatusLogResponse> statusTimeline;
    private List<CommentResponse> comments;
    private List<TimelineEventResponse> timeline;
}
