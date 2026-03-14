package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * User Entity for Authentication
 * Stores user credentials and profile information
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password; // This will store BCrypt encrypted password

    @Column(name = "role", nullable = false)
    private String role = "USER"; // Default role: USER, can be ADMIN, STAFF, SUPERADMIN

    @Column(name = "staff_id", unique = true, length = 32)
    private String staffId;

    // `rank` is a reserved keyword in MySQL, so keep the field name but escape the column identifier.
    @Column(name = "`rank`", length = 64)
    private String rank;

    @Column(name = "specialization_category", length = 120)
    private String specializationCategory;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor for creating new users during registration
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "USER";
        this.isActive = true;
    }
}
