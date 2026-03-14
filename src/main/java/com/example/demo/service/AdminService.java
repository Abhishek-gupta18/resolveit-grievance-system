package com.example.demo.service;

import com.example.demo.dto.AdminUserRequest;
import com.example.demo.dto.AdminUserResponse;
import com.example.demo.model.Complaint;
import com.example.demo.model.User;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
public class AdminService {

    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ADMIN", "STAFF");
    private static final String SENIOR_HANDLER_RANK = "Senior Handler";
    private static final Set<String> KNOWN_COMPLAINT_CATEGORIES = Set.of(
            "network",
            "wifi-connectivity",
            "server-malfunction",
            "software-bug",
            "application-installation",
            "access-issue",
            "password-reset",
            "email-issue",
            "vpn-remote-access",
            "hardware-failure",
            "printer-scanner",
            "cybersecurity",
            "data-backup-recovery",
            "performance-slow-system",
            "it-support-request",
            "other"
    );
    private static final String SPECIAL_ADMIN_EMAIL = "abhishekgupta.1856@outlook.com";
    private static final String STAFF_ID_PREFIX = "STF";
    private static final String RANDOM_ALNUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String DEFAULT_STAFF_PASSWORD = "password";
    private final Random random = new Random();

    public List<AdminUserResponse> getAllUsers(String authenticatedEmail) {
        User adminUser = getAdminUser(authenticatedEmail);
        if (adminUser == null) {
            throw new RuntimeException("Access denied");
        }

        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AdminUserResponse createUser(AdminUserRequest request, String authenticatedEmail) {
        getAdminUser(authenticatedEmail);

        String name = normalizeRequired(request.getName(), "Name is required");
        String email = normalizeEmail(request.getEmail());
        String password = request.getPassword() == null ? "" : request.getPassword().trim();
        if (password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        String role = normalizeRole(request.getRole());
        validateRoleEmailCombination(role, email);
        user.setRole(role);
        if ("STAFF".equals(role)) {
            user.setStaffId(generateStaffId());
            String rank = normalizeStaffRank(request.getRank());
            String specialization = normalizeSpecializationCategory(request.getSpecializationCategory());
            if (SENIOR_HANDLER_RANK.equals(rank) && specialization == null) {
                throw new RuntimeException("Specialization category is required for Senior Handler rank");
            }
            user.setRank(rank);
            user.setSpecializationCategory(specialization);
        } else {
            user.setRank(null);
            user.setSpecializationCategory(null);
        }
        user.setIsActive(request.getIsActive() == null || request.getIsActive());

        return toResponse(userRepository.save(user));
    }

    public AdminUserResponse updateUser(Long userId, AdminUserRequest request, String authenticatedEmail) {
        User adminUser = getAdminUser(authenticatedEmail);
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) {
            targetUser.setName(normalizeRequired(request.getName(), "Name is required"));
        }

        if (request.getEmail() != null) {
            String normalizedEmail = normalizeEmail(request.getEmail());
            if (userRepository.existsByEmailAndIdNot(normalizedEmail, userId)) {
                throw new RuntimeException("Email already registered");
            }
            targetUser.setEmail(normalizedEmail);
        }

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            String password = request.getPassword().trim();
            if (password.length() < 6) {
                throw new RuntimeException("Password must be at least 6 characters");
            }
            targetUser.setPassword(passwordEncoder.encode(password));
        }

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            String nextRole = normalizeRole(request.getRole());
            if (adminUser.getId().equals(targetUser.getId()) && !"ADMIN".equals(nextRole)) {
                throw new RuntimeException("You cannot remove your own ADMIN role");
            }
            targetUser.setRole(nextRole);
            if ("STAFF".equals(nextRole) && (targetUser.getStaffId() == null || targetUser.getStaffId().isBlank())) {
                targetUser.setStaffId(generateStaffId());
            }
            if ("ADMIN".equals(nextRole) || "USER".equals(nextRole)) {
                targetUser.setStaffId(null);
                targetUser.setRank(null);
                targetUser.setSpecializationCategory(null);
            }
        }

        String effectiveRole = targetUser.getRole() == null ? "USER" : targetUser.getRole().trim().toUpperCase(Locale.ENGLISH);
        if ("STAFF".equals(effectiveRole)) {
            String nextRank = targetUser.getRank();
            if (request.getRank() != null) {
                nextRank = normalizeStaffRank(request.getRank());
                targetUser.setRank(nextRank);
            } else if (targetUser.getRank() == null || targetUser.getRank().isBlank()) {
                targetUser.setRank("Handler");
                nextRank = "Handler";
            }

            String nextSpecialization = targetUser.getSpecializationCategory();
            if (request.getSpecializationCategory() != null) {
                nextSpecialization = normalizeSpecializationCategory(request.getSpecializationCategory());
                targetUser.setSpecializationCategory(nextSpecialization);
            } else if (targetUser.getSpecializationCategory() != null && targetUser.getSpecializationCategory().isBlank()) {
                targetUser.setSpecializationCategory(null);
                nextSpecialization = null;
            }

            if (SENIOR_HANDLER_RANK.equals(nextRank) && nextSpecialization == null) {
                throw new RuntimeException("Specialization category is required for Senior Handler rank");
            }
        }

        validateRoleEmailCombination(targetUser.getRole(), targetUser.getEmail());

        if (request.getIsActive() != null) {
            if (adminUser.getId().equals(targetUser.getId()) && !request.getIsActive()) {
                throw new RuntimeException("You cannot deactivate your own account");
            }
            targetUser.setIsActive(request.getIsActive());
        }

        return toResponse(userRepository.save(targetUser));
    }

    public Map<String, Object> addSampleStaffAndAssignComplaints(String authenticatedEmail) {
        getAdminUser(authenticatedEmail);

        List<AdminUserRequest> sampleStaff = Arrays.asList(
            new AdminUserRequest("Aarav Verma", "aarav001@staff.com", DEFAULT_STAFF_PASSWORD, "STAFF", "Handler", null, true),
            new AdminUserRequest("Meera Iyer", "meera002@staff.com", DEFAULT_STAFF_PASSWORD, "STAFF", "Handler", null, true),
            new AdminUserRequest("Rohit Das", "rohit003@staff.com", DEFAULT_STAFF_PASSWORD, "STAFF", "Handler", null, true),
            new AdminUserRequest("Nisha Kapoor", "nisha004@staff.com", DEFAULT_STAFF_PASSWORD, "STAFF", "Handler", null, true),
            new AdminUserRequest("Kabir Sharma", "kabir005@staff.com", DEFAULT_STAFF_PASSWORD, "STAFF", "Handler", null, true)
        );

        int createdStaffCount = 0;
        List<AdminUserResponse> createdUsers = new ArrayList<>();

        for (AdminUserRequest staffRequest : sampleStaff) {
            String email = normalizeEmail(staffRequest.getEmail());
            if (userRepository.existsByEmail(email)) {
                continue;
            }

            User user = new User();
            user.setName(normalizeRequired(staffRequest.getName(), "Name is required"));
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(staffRequest.getPassword()));
            user.setRole("STAFF");
            user.setStaffId(generateStaffId());
            user.setRank("Handler");
            user.setSpecializationCategory(null);
            user.setIsActive(true);

            User saved = userRepository.save(user);
            createdStaffCount++;
            createdUsers.add(toResponse(saved));
        }

        int createdSeniorHandlerCount = 0;
        for (String category : KNOWN_COMPLAINT_CATEGORIES) {
            String email = ("senior." + category + "@staff.com").toLowerCase(Locale.ENGLISH);
            if (userRepository.existsByEmail(email)) {
                continue;
            }

            User seniorHandler = new User();
            seniorHandler.setName(getSeniorHandlerDisplayName(category));
            seniorHandler.setEmail(email);
            seniorHandler.setPassword(passwordEncoder.encode(DEFAULT_STAFF_PASSWORD));
            seniorHandler.setRole("STAFF");
            seniorHandler.setStaffId(generateStaffId());
            seniorHandler.setRank(SENIOR_HANDLER_RANK);
            seniorHandler.setSpecializationCategory(category);
            seniorHandler.setIsActive(true);

            userRepository.save(seniorHandler);
            createdSeniorHandlerCount++;
        }

        List<User> activeHandlers = userRepository.findByRoleInAndIsActiveTrueOrderByCreatedAtAsc(List.of("STAFF", "ADMIN"));
        List<User> primaryHandlers = activeHandlers.stream()
                .filter(handler -> !isSeniorHandler(handler))
                .toList();
        List<Complaint> unassignedComplaints = complaintRepository.findByAssignedStaffIsNullOrderByCreatedAtAsc();

        int assignedComplaintCount = 0;
        if (!primaryHandlers.isEmpty() && !unassignedComplaints.isEmpty()) {
            int index = 0;
            for (Complaint complaint : unassignedComplaints) {
                User assignee = primaryHandlers.get(index % primaryHandlers.size());
                complaint.setAssignedStaff(assignee);
                index++;
                assignedComplaintCount++;
            }
            complaintRepository.saveAll(unassignedComplaints);
        }

        return Map.of(
                "createdStaffCount", createdStaffCount,
                "createdSeniorHandlerCount", createdSeniorHandlerCount,
                "assignedComplaintCount", assignedComplaintCount,
                "createdUsers", createdUsers
        );
    }

    private User getAdminUser(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        String role = user.getRole() == null ? "USER" : user.getRole().trim().toUpperCase(Locale.ENGLISH);
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied");
        }

        return user;
    }

    private String normalizeRole(String role) {
        String normalizedRole = normalizeRequired(role, "Role is required").toUpperCase(Locale.ENGLISH);
        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new RuntimeException("Invalid role. Must be one of: USER, ADMIN, STAFF");
        }
        return normalizedRole;
    }

    private String normalizeRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
        return value.trim();
    }

    private String normalizeEmail(String email) {
        String normalizedEmail = normalizeRequired(email, "Email is required").toLowerCase(Locale.ENGLISH);
        if (!normalizedEmail.contains("@") || !normalizedEmail.contains(".")) {
            throw new RuntimeException("A valid email is required");
        }
        return normalizedEmail;
    }

    private void validateRoleEmailCombination(String role, String email) {
        if (role == null || email == null) {
            return;
        }

        String normalizedRole = role.toUpperCase(Locale.ENGLISH);
        String normalizedEmail = email.toLowerCase(Locale.ENGLISH);

        if ("ADMIN".equals(normalizedRole) && !normalizedEmail.endsWith("@admin.com")) {
            if (SPECIAL_ADMIN_EMAIL.equals(normalizedEmail)) {
                return;
            }
            throw new RuntimeException("ADMIN users must use @admin.com email");
        }
        if ("STAFF".equals(normalizedRole) && !normalizedEmail.endsWith("@staff.com")) {
            throw new RuntimeException("STAFF users must use @staff.com email");
        }
        if ("USER".equals(normalizedRole)
                && (normalizedEmail.endsWith("@admin.com") || normalizedEmail.endsWith("@staff.com"))) {
            throw new RuntimeException("USER accounts cannot use @admin.com or @staff.com email");
        }
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getStaffId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getRank(),
                user.getSpecializationCategory(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private String normalizeStaffRank(String rank) {
        if (rank == null || rank.trim().isEmpty()) {
            return "Handler";
        }
        String normalized = rank.trim();
        if (SENIOR_HANDLER_RANK.equalsIgnoreCase(normalized)) {
            return SENIOR_HANDLER_RANK;
        }
        return normalized;
    }

    private String normalizeSpecializationCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        String normalized = category.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.contains("hostel") || normalized.contains("academic") || normalized.contains("infrastructure")) {
            normalized = "it-support-request";
        }

        if (!KNOWN_COMPLAINT_CATEGORIES.contains(normalized)) {
            throw new RuntimeException("Invalid specialization category");
        }

        return normalized;
    }

    private String getSeniorHandlerDisplayName(String category) {
        String readable = category.replace('-', ' ');
        String[] parts = readable.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder + " Senior Handler";
    }

    private boolean isSeniorHandler(User user) {
        if (user == null || user.getRank() == null) {
            return false;
        }
        return SENIOR_HANDLER_RANK.equalsIgnoreCase(user.getRank().trim());
    }

    private String generateStaffId() {
        String id;
        int attempts = 0;
        do {
            StringBuilder randomPart = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                randomPart.append(RANDOM_ALNUM.charAt(random.nextInt(RANDOM_ALNUM.length())));
            }
            id = STAFF_ID_PREFIX + randomPart.toString().toUpperCase(Locale.ENGLISH);
            attempts++;
            if (attempts > 20) {
                throw new RuntimeException("Could not generate unique staff ID");
            }
        } while (userRepository.existsByStaffId(id));

        return id;
    }
}