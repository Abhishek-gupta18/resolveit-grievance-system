package com.example.demo.repository;

import com.example.demo.model.Escalation;
import com.example.demo.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscalationRepository extends JpaRepository<Escalation, Long> {
	boolean existsByComplaintAndIsResolvedFalse(Complaint complaint);
}
