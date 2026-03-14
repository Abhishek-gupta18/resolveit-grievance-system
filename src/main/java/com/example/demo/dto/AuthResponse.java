package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Authentication Response
 * Returned after successful login or registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String staffId;
    private String name;
    private String email;
    private String role;
    private String message;

    // Constructor for successful login with token
    public AuthResponse(String token, Long userId, String staffId, String name, String email, String role) {
        this.token = token;
        this.type = "Bearer";
        this.userId = userId;
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Constructor for registration success without token
    public AuthResponse(String message, Long userId, String email) {
        this.message = message;
        this.userId = userId;
        this.email = email;
    }
}
