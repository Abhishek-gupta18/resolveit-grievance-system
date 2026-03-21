package com.example.demo.controller;

import com.example.demo.dto.ComplaintRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.ComplaintStatusUpdateRequest;
import com.example.demo.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<?> createComplaint(@RequestBody ComplaintRequest request, Authentication authentication) {
        try {
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description is required");
            }
            if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Category is required");
            }

            String authenticatedEmail = authentication.getName();
            ComplaintResponse response = complaintService.createComplaint(request, authenticatedEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create complaint: " + e.getMessage());
        }
    }

    @PostMapping(value = "/with-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createComplaintWithProof(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "isAnonymous", required = false) Boolean isAnonymous,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "urgency", required = false) String urgency,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestPart(value = "proofFile", required = false) MultipartFile proofFile,
            Authentication authentication
    ) {
        try {
            ComplaintRequest request = new ComplaintRequest();
            request.setUserId(userId);
            request.setIsAnonymous(isAnonymous);
            request.setCategory(category);
            request.setDescription(description);
            request.setUrgency(urgency);
            request.setPriority(priority);

            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description is required");
            }
            if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Category is required");
            }

            String authenticatedEmail = authentication.getName();
            ComplaintResponse response = complaintService.createComplaint(request, authenticatedEmail, proofFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create complaint: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyComplaints(Authentication authentication) {
        try {
            String authenticatedEmail = authentication.getName();
            List<ComplaintResponse> complaints = complaintService.getComplaintsForUser(authenticatedEmail);
            return ResponseEntity.ok(complaints);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch complaints: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllComplaints(Authentication authentication) {
        try {
            String authenticatedEmail = authentication.getName();
            List<ComplaintResponse> complaints = complaintService.getAllComplaintsForAdmin(authenticatedEmail);
            return ResponseEntity.ok(complaints);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch all complaints: " + e.getMessage());
        }
    }

    @GetMapping("/{complaintId}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long complaintId, Authentication authentication) {
        try {
            String authenticatedEmail = authentication.getName();
            ComplaintResponse complaint = complaintService.getComplaintByIdForUser(complaintId, authenticatedEmail);
            return ResponseEntity.ok(complaint);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch complaint: " + e.getMessage());
        }
    }

    @GetMapping("/{complaintId}/proof")
    public ResponseEntity<?> viewProofFile(@PathVariable Long complaintId, Authentication authentication) {
        try {
            String authenticatedEmail = authentication.getName();
            Resource resource = complaintService.getProofFileResource(complaintId, authenticatedEmail);
            String fileName = complaintService.getProofFileName(complaintId);
            String contentType = complaintService.getProofFileType(complaintId);

            ContentDisposition disposition = ContentDisposition.inline().filename(fileName).build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                    .contentType(contentType == null || contentType.isBlank()
                            ? MediaType.APPLICATION_OCTET_STREAM
                            : MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load proof file: " + e.getMessage());
        }
    }

    @PutMapping("/{complaintId}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestBody ComplaintStatusUpdateRequest request,
            Authentication authentication
    ) {
        try {
            String authenticatedEmail = authentication.getName();
            ComplaintResponse response = complaintService.updateComplaintStatus(
                    complaintId,
                    request.getStatus(),
                    request.getAdminReview(),
                    request.getAssignedStaffId(),
                    authenticatedEmail
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update complaint status: " + e.getMessage());
        }
    }
}
