package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TimelineEventResponse {
    private String eventType;
    private String status;
    private String comment;
    private String actorName;
    private LocalDateTime occurredAt;
}
