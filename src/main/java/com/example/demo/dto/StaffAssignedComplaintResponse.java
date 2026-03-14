package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffAssignedComplaintResponse {
    private Long id;
    private String complaintCode;
    private String category;
    private String description;
    private String urgency;
    private String status;
    private String userName;
    private String createdAt;
    private String updatedAt;
}
