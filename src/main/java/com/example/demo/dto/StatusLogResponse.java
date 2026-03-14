package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StatusLogResponse {
    private String status;
    private String comment;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
