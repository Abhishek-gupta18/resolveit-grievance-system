package com.example.demo.dto;

import lombok.Data;

@Data
public class StaffAttendanceRequest {
    private Long staffUserId;
    private String attendanceDate;
    private String status;
    private String checkInTime;
    private String checkOutTime;
    private String notes;
}
