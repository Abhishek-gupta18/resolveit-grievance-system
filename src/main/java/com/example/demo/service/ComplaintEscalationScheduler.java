package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ComplaintEscalationScheduler {

    private final ComplaintService complaintService;

    @Value("${app.escalation.unresolved-days:3}")
    private int unresolvedDays;

    public ComplaintEscalationScheduler(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Runs once every day at 1 AM server time.
    @Scheduled(cron = "${app.escalation.cron:0 0 1 * * *}")
    public void autoEscalateUnresolvedComplaints() {
        complaintService.autoEscalateOverdueComplaints(unresolvedDays);
    }
}
