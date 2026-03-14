package com.example.demo.dto;

import lombok.Data;

@Data
public class ComplaintRequest {
    private Long userId;
    private Boolean isAnonymous;
    private String category;
    private String description;
    private String urgency;
    // Frontend currently sends "priority"; backend maps it as fallback for urgency.
    private String priority;
}
