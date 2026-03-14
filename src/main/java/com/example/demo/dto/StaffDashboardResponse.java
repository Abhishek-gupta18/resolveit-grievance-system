package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StaffDashboardResponse {
    private String staffId;
    private String name;
    private String email;
    private String rank;
    private String specializationCategory;
    private long todayAssignedCount;
    private long completedTodayCount;
    private long completedMonthCount;
    private StaffAttendanceResponse todayAttendance;
    private List<StaffAssignedComplaintResponse> assignedComplaints;
    private List<StaffAttendanceResponse> recentAttendance;
}
