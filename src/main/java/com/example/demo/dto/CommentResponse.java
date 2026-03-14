package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long complaintId;
    private Long adminUserId;
    private String adminName;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
