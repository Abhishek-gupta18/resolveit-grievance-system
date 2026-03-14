package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complaints/{complaintId}/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping
    public ResponseEntity<?> getComments(
            @PathVariable Long complaintId,
            Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            List<CommentResponse> comments = complaintService.getCommentsForComplaint(complaintId, authenticatedEmail);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch comments: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addComment(
            @PathVariable Long complaintId,
            @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            CommentResponse response = complaintService.addAdminComment(
                    complaintId,
                    request.getComment(),
                    authenticatedEmail
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save comment: " + e.getMessage());
        }
    }
}
