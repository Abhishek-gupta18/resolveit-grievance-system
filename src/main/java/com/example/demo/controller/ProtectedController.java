package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Example Protected Controller
 * Demonstrates how to create endpoints that require JWT authentication
 */
@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    /**
     * Example protected endpoint
     * Only accessible with valid JWT token
     * GET /api/protected/user-info
     */
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        // Get authentication details from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName(); // This is the email from JWT
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("role", role);
            response.put("message", "You are authenticated!");
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).body("Not authenticated");
    }

    /**
     * Example endpoint that requires specific role
     * Only accessible to users with ADMIN role
     */
    @GetMapping("/admin-only")
    public ResponseEntity<?> adminOnly() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return ResponseEntity.ok("Welcome Admin! This is a protected admin-only endpoint.");
        }
        
        return ResponseEntity.status(403).body("Access denied. Admin role required.");
    }
}
