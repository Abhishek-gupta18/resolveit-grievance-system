package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffAttendanceResponse {
    private Long id;
    private String attendanceDate;
    private String status;
    private String checkInTime;
    private String checkOutTime;
    private String notes;
    private String markedByName;
}
