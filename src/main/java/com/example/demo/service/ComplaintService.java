package com.example.demo.service;

import com.example.demo.dto.ComplaintRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.StatusLogResponse;
import com.example.demo.dto.TimelineEventResponse;
import com.example.demo.model.Complaint;
import com.example.demo.model.ComplaintComment;
import com.example.demo.model.Escalation;
import com.example.demo.model.StatusLog;
import com.example.demo.model.User;
import com.example.demo.repository.ComplaintCommentRepository;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.EscalationRepository;
import com.example.demo.repository.StatusLogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
public class ComplaintService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "pdf", "doc", "docx");
    private static final Set<String> VALID_STATUSES = Set.of("open", "inprogress", "resolved", "pending", "escalated");
    private static final DateTimeFormatter COMPLAINT_CODE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String COMPLAINT_CODE_PREFIX = "CMP";
    private static final String RANDOM_ALNUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SENIOR_HANDLER_RANK = "Senior Handler";
    private final Random random = new Random();

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private StatusLogRepository statusLogRepository;

    @Autowired
    private ComplaintCommentRepository complaintCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EscalationRepository escalationRepository;

    @Value("${app.upload.dir:uploads/complaint-proofs}")
    private String uploadDir;

    public ComplaintResponse createComplaint(ComplaintRequest request, String authenticatedEmail) {
        return createComplaint(request, authenticatedEmail, null);
    }

    public ComplaintResponse createComplaint(ComplaintRequest request, String authenticatedEmail, MultipartFile proofFile) {
        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Allow admins/staff to post for any userId, normal users can only post for themselves.
        User complaintOwner = authenticatedUser;
        if (request.getUserId() != null && !request.getUserId().equals(authenticatedUser.getId())) {
            String role = authenticatedUser.getRole() != null ? authenticatedUser.getRole().toUpperCase() : "USER";
            if ("ADMIN".equals(role) || "STAFF".equals(role)) {
                complaintOwner = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new RuntimeException("Target user not found"));
            }
        }

        String urgency = (request.getUrgency() != null && !request.getUrgency().isBlank())
                ? request.getUrgency().trim().toLowerCase()
                : (request.getPriority() != null ? request.getPriority().trim().toLowerCase() : "normal");

        if (!"low".equals(urgency) && !"normal".equals(urgency) && !"high".equals(urgency) && !"urgent".equals(urgency)) {
            urgency = "normal";
        }

        Complaint complaint = new Complaint();
        complaint.setUser(complaintOwner);
        complaint.setComplaintCode(generateComplaintCode());
        complaint.setIsAnonymous(Boolean.TRUE.equals(request.getIsAnonymous()));
        complaint.setCategory(normalizeCategory(request.getCategory()));
        complaint.setDescription(request.getDescription());
        complaint.setUrgency(urgency);
        complaint.setStatus("open");

        if (proofFile != null && !proofFile.isEmpty()) {
            storeProofFile(complaint, proofFile);
        }

        Complaint saved = complaintRepository.save(complaint);

        StatusLog statusLog = new StatusLog();
        statusLog.setComplaint(saved);
        statusLog.setStatus("open");
        statusLog.setComment("Complaint created");
        statusLog.setUpdatedBy(authenticatedUser);
        statusLogRepository.save(statusLog);

        return toResponse(saved);
    }

    public List<ComplaintResponse> getAllComplaintsForAdmin(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            throw new RuntimeException("Access denied");
        }

        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();
        return sortByPriorityThenDate(complaints).stream().map(this::toResponse).toList();
    }

    public Resource getProofFileResource(Long complaintId, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        boolean owner = complaint.getUser().getId().equals(user.getId());
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        boolean adminOrStaff = "ADMIN".equals(role) || "STAFF".equals(role);

        if (!owner && !adminOrStaff) {
            throw new RuntimeException("Access denied");
        }

        if (complaint.getProofFilePath() == null || complaint.getProofFilePath().isBlank()) {
            throw new RuntimeException("No proof file found for this complaint");
        }

        Path filePath = Paths.get(complaint.getProofFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Proof file no longer exists on server");
        }

        return new FileSystemResource(filePath);
    }

    public String getProofFileName(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return complaint.getProofFileName();
    }

    public String getProofFileType(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return complaint.getProofFileType();
    }

    public List<ComplaintResponse> getComplaintsForUser(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        List<Complaint> complaints = complaintRepository.findByUserOrderByCreatedAtDesc(user);
        return sortByPriorityThenDate(complaints).stream().map(this::toResponse).toList();
    }

    public ComplaintResponse getComplaintByIdForUser(Long complaintId, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Complaint complaint = getComplaintWithAccessCheck(complaintId, user);
        return toResponse(complaint);
    }

    public List<CommentResponse> getCommentsForComplaint(Long complaintId, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        Complaint complaint = getComplaintWithAccessCheck(complaintId, user);

        return complaintCommentRepository.findByComplaintOrderByCreatedAtAsc(complaint)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    public CommentResponse addAdminComment(Long complaintId, String commentText, String authenticatedEmail) {
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new RuntimeException("Comment is required");
        }

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            throw new RuntimeException("Access denied");
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        ComplaintComment comment = new ComplaintComment();
        comment.setComplaint(complaint);
        comment.setAdminUser(user);
        comment.setComment(commentText.trim());

        ComplaintComment saved = complaintCommentRepository.save(comment);
        return toCommentResponse(saved);
    }

        public ComplaintResponse updateComplaintStatus(
            Long complaintId,
            String status,
            String adminReview,
            Long assignedStaffId,
            String authenticatedEmail
        ) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            throw new RuntimeException("Access denied");
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        String normalizedStatus = normalizeStatus(status);
        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new RuntimeException("Invalid status. Allowed: open, inprogress, pending, escalated, resolved");
        }

        if (!isValidTransition(complaint.getStatus(), normalizedStatus)) {
            throw new RuntimeException("Invalid status transition for selected status update");
        }

        String reviewText = adminReview != null ? adminReview.trim() : null;
        if ("resolved".equals(normalizedStatus)) {
            String effectiveReview = reviewText;
            if ((effectiveReview == null || effectiveReview.isBlank()) && complaint.getAdminReview() != null) {
                effectiveReview = complaint.getAdminReview().trim();
            }
            if (effectiveReview == null || effectiveReview.isBlank()) {
                throw new RuntimeException("Admin review is required when status is resolved");
            }
        }

        complaint.setStatus(normalizedStatus);

        if (assignedStaffId != null) {
            User assignee = userRepository.findById(assignedStaffId)
                    .orElseThrow(() -> new RuntimeException("Assigned staff user not found"));

            String assigneeRole = assignee.getRole() != null ? assignee.getRole().toUpperCase() : "USER";
            if (!"STAFF".equals(assigneeRole) && !"ADMIN".equals(assigneeRole)) {
                throw new RuntimeException("Assigned user must have STAFF or ADMIN role");
            }
            if (!Boolean.TRUE.equals(assignee.getIsActive())) {
                throw new RuntimeException("Assigned staff account is inactive");
            }

            complaint.setAssignedStaff(assignee);
        }

        if ("escalated".equals(normalizedStatus)) {
            User seniorHandler = findSeniorHandlerForCategory(complaint.getCategory());
            complaint.setAssignedStaff(seniorHandler);
        }

        if (reviewText != null) {
            complaint.setAdminReview(reviewText);
        }
        if ("resolved".equals(normalizedStatus)) {
            complaint.setResolvedAt(LocalDateTime.now());
        } else {
            complaint.setResolvedAt(null);
        }

        Complaint updated = complaintRepository.save(complaint);

        StatusLog statusLog = new StatusLog();
        statusLog.setComplaint(updated);
        statusLog.setStatus(normalizedStatus);
        statusLog.setComment((reviewText != null && !reviewText.isBlank())
            ? reviewText
                : "Status changed to " + normalizedStatus);
        statusLog.setUpdatedBy(user);
        statusLogRepository.save(statusLog);

        if (reviewText != null && !reviewText.isBlank()) {
            ComplaintComment complaintComment = new ComplaintComment();
            complaintComment.setComplaint(updated);
            complaintComment.setAdminUser(user);
            complaintComment.setComment(reviewText);
            complaintCommentRepository.save(complaintComment);
        }

        return toResponse(updated);
    }

    @Transactional
    public int autoEscalateOverdueComplaints(int unresolvedDays) {
        if (unresolvedDays <= 0) {
            unresolvedDays = 3;
        }

        LocalDateTime cutoff = LocalDateTime.now().minusDays(unresolvedDays);
        List<Complaint> overdueComplaints = complaintRepository
                .findByAssignedStaffIsNotNullAndStatusInAndCreatedAtBeforeOrderByCreatedAtAsc(
                        List.of("open", "inprogress", "pending", "escalated"),
                        cutoff
                );

        int escalatedCount = 0;
        for (Complaint complaint : overdueComplaints) {
            User currentAssignee = complaint.getAssignedStaff();
            User higherAssignee = findHigherStaffAssignee(currentAssignee, complaint.getCategory());

            if (higherAssignee == null || currentAssignee == null
                    || higherAssignee.getId().equals(currentAssignee.getId())) {
                continue;
            }

            complaint.setAssignedStaff(higherAssignee);
            complaint.setStatus("escalated");
            complaintRepository.save(complaint);

            StatusLog statusLog = new StatusLog();
            statusLog.setComplaint(complaint);
            statusLog.setStatus("escalated");
            statusLog.setComment("Auto-escalated after " + unresolvedDays + " days unresolved to "
                    + higherAssignee.getName());
            statusLog.setUpdatedBy(higherAssignee);
            statusLogRepository.save(statusLog);

            Escalation escalation = new Escalation();
            escalation.setComplaint(complaint);
            escalation.setEscalatedTo(higherAssignee);
            escalation.setReason("Auto-escalated after " + unresolvedDays + " days unresolved");
            escalation.setIsResolved(false);
            escalationRepository.save(escalation);

            escalatedCount++;
        }

        return escalatedCount;
    }

    private ComplaintResponse toResponse(Complaint complaint) {
        ensureComplaintCode(complaint);

        List<StatusLogResponse> statusTimeline = statusLogRepository.findByComplaintOrderByUpdatedAtAsc(complaint)
                .stream()
                .map(log -> new StatusLogResponse(
                        normalizeStatus(log.getStatus()),
                        log.getComment(),
                        log.getUpdatedBy() != null ? log.getUpdatedBy().getName() : null,
                        log.getUpdatedAt()
                ))
                .toList();

            List<CommentResponse> comments = complaintCommentRepository.findByComplaintOrderByCreatedAtAsc(complaint)
                .stream()
                .map(this::toCommentResponse)
                .toList();

            List<TimelineEventResponse> unifiedTimeline = new ArrayList<>();
            for (StatusLog log : statusLogRepository.findByComplaintOrderByUpdatedAtAsc(complaint)) {
                unifiedTimeline.add(new TimelineEventResponse(
                    "status",
                    normalizeStatus(log.getStatus()),
                    log.getComment(),
                    log.getUpdatedBy() != null ? log.getUpdatedBy().getName() : null,
                    log.getUpdatedAt()
                ));
            }
            for (ComplaintComment comment : complaintCommentRepository.findByComplaintOrderByCreatedAtAsc(complaint)) {
                unifiedTimeline.add(new TimelineEventResponse(
                    "comment",
                    null,
                    comment.getComment(),
                    comment.getAdminUser() != null ? comment.getAdminUser().getName() : null,
                    comment.getCreatedAt()
                ));
            }
            unifiedTimeline.sort(Comparator.comparing(TimelineEventResponse::getOccurredAt,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return new ComplaintResponse(
                complaint.getId(),
                complaint.getComplaintCode(),
                complaint.getUser().getId(),
                complaint.getUser().getName(),
                complaint.getUser().getEmail(),
                complaint.getAssignedStaff() != null ? complaint.getAssignedStaff().getId() : null,
                complaint.getAssignedStaff() != null ? complaint.getAssignedStaff().getName() : null,
                complaint.getAssignedStaff() != null ? complaint.getAssignedStaff().getEmail() : null,
                complaint.getIsAnonymous(),
                complaint.getCategory(),
                complaint.getDescription(),
                complaint.getUrgency(),
                normalizeStatus(complaint.getStatus()),
                complaint.getAdminReview(),
                complaint.getProofFileName(),
                complaint.getProofFileType(),
                complaint.getProofFileName() != null ? "/api/complaints/" + complaint.getId() + "/proof" : null,
                complaint.getCreatedAt(),
                complaint.getUpdatedAt(),
                complaint.getResolvedAt(),
                statusTimeline,
                comments,
                unifiedTimeline
        );
    }

    private CommentResponse toCommentResponse(ComplaintComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getComplaint().getId(),
                comment.getAdminUser() != null ? comment.getAdminUser().getId() : null,
                comment.getAdminUser() != null ? comment.getAdminUser().getName() : null,
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    private String generateComplaintCode() {
        String code;
        int attempts = 0;
        do {
            StringBuilder randomPart = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                randomPart.append(RANDOM_ALNUM.charAt(random.nextInt(RANDOM_ALNUM.length())));
            }
            code = COMPLAINT_CODE_PREFIX + LocalDateTime.now().format(COMPLAINT_CODE_DATE) + randomPart;
            attempts++;
            if (attempts > 20) {
                throw new RuntimeException("Could not generate unique complaint code");
            }
        } while (complaintRepository.existsByComplaintCode(code));

        return code;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "open";
        }
        String normalized = status.trim().toLowerCase();
        if ("submitted".equals(normalized)) {
            return "open";
        }
        if ("in-progress".equals(normalized) || "in progress".equals(normalized) || "under_progress".equals(normalized)
                || "under progress".equals(normalized)) {
            return "inprogress";
        }
        if ("done".equals(normalized) || "closed".equals(normalized)) {
            return "resolved";
        }
        return normalized;
    }

    private boolean isValidTransition(String currentStatus, String targetStatus) {
        String current = normalizeStatus(currentStatus);
        if (current.equals(targetStatus)) {
            return true;
        }
        if ("resolved".equals(current)) {
            return false;
        }

        if ("open".equals(current)) {
            return Set.of("inprogress", "pending", "escalated", "resolved").contains(targetStatus);
        }
        if ("pending".equals(current)) {
            return Set.of("open", "inprogress", "escalated", "resolved").contains(targetStatus);
        }
        if ("inprogress".equals(current)) {
            return Set.of("pending", "escalated", "resolved").contains(targetStatus);
        }
        if ("escalated".equals(current)) {
            return Set.of("inprogress", "pending", "resolved").contains(targetStatus);
        }

        return false;
    }

    private List<Complaint> sortByPriorityThenDate(List<Complaint> complaints) {
        return complaints.stream()
                .sorted(Comparator
                        .comparingInt((Complaint c) -> priorityRank(c.getUrgency())).reversed()
                        .thenComparing(Complaint::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private int priorityRank(String urgency) {
        if (urgency == null) {
            return 2;
        }
        return switch (urgency.trim().toLowerCase()) {
            case "urgent" -> 4;
            case "high" -> 3;
            case "normal" -> 2;
            case "low" -> 1;
            default -> 2;
        };
    }

    private void storeProofFile(Complaint complaint, MultipartFile proofFile) {
        if (proofFile.getOriginalFilename() == null || proofFile.getOriginalFilename().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }

        String originalName = proofFile.getOriginalFilename().trim();
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported file type. Allowed: jpg, jpeg, png, pdf, doc, docx");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            String storedName = UUID.randomUUID() + "_" + originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path targetPath = uploadPath.resolve(storedName).normalize();
            Files.copy(proofFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            complaint.setProofFileName(originalName);
            complaint.setProofFileType(proofFile.getContentType());
            complaint.setProofFilePath(targetPath.toString());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store proof file");
        }
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase(Locale.ENGLISH);
    }

    private void ensureComplaintCode(Complaint complaint) {
        if (complaint.getComplaintCode() != null && !complaint.getComplaintCode().isBlank()) {
            return;
        }
        complaint.setComplaintCode(generateComplaintCode());
        complaintRepository.save(complaint);
    }

    private Complaint getComplaintWithAccessCheck(Long complaintId, User user) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        boolean adminOrStaff = "ADMIN".equals(role) || "STAFF".equals(role);
        boolean owner = complaint.getUser() != null && complaint.getUser().getId().equals(user.getId());

        if (!adminOrStaff && !owner) {
            throw new RuntimeException("Access denied");
        }

        return complaint;
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "it-support-request";
        }

        String normalized = category.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.contains("hostel") || normalized.contains("academic") || normalized.contains("infrastructure")) {
            return "it-support-request";
        }

        return normalized;
    }

    private User findSeniorHandlerForCategory(String category) {
        String normalizedCategory = normalizeCategory(category);
        return userRepository.findFirstByRoleAndRankAndSpecializationCategoryAndIsActiveTrueOrderByCreatedAtAsc(
                        "STAFF",
                        SENIOR_HANDLER_RANK,
                        normalizedCategory
                )
                .orElseThrow(() -> new RuntimeException(
                        "No active Senior Handler found for category: " + normalizedCategory
                ));
    }

    private User findHigherStaffAssignee(User currentAssignee, String complaintCategory) {
        if (currentAssignee == null) {
            return null;
        }

        String role = currentAssignee.getRole() == null
                ? "USER"
                : currentAssignee.getRole().trim().toUpperCase(Locale.ENGLISH);

        if ("STAFF".equals(role)) {
            if (!SENIOR_HANDLER_RANK.equalsIgnoreCase(
                    currentAssignee.getRank() == null ? "" : currentAssignee.getRank().trim())) {
                try {
                    return findSeniorHandlerForCategory(complaintCategory);
                } catch (RuntimeException ignored) {
                    // Fall back to first active admin when a category-specific senior handler is unavailable.
                }
            }

            return userRepository.findByRoleAndIsActiveTrueOrderByCreatedAtAsc("ADMIN")
                    .stream()
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
