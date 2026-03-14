package com.example.demo.dto;

import lombok.Data;

@Data
public class ComplaintStatusUpdateRequest {
    private String status;
    private String adminReview;
    private Long assignedStaffId;
}
