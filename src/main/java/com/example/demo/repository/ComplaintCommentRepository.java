package com.example.demo.repository;

import com.example.demo.model.Complaint;
import com.example.demo.model.ComplaintComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintCommentRepository extends JpaRepository<ComplaintComment, Long> {
    List<ComplaintComment> findByComplaintOrderByCreatedAtAsc(Complaint complaint);
}
