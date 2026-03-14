package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides database operations for user authentication
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     * Used for login and checking if user already exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email already exists
     * Used during registration to prevent duplicate accounts
     */
    Boolean existsByEmail(String email);

    Boolean existsByStaffId(String staffId);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<User> findAllByOrderByCreatedAtDesc();

    List<User> findByRoleInAndIsActiveTrueOrderByCreatedAtAsc(List<String> roles);

    List<User> findByRoleAndIsActiveTrueOrderByCreatedAtAsc(String role);

    Optional<User> findFirstByRoleAndRankAndSpecializationCategoryAndIsActiveTrueOrderByCreatedAtAsc(
            String role,
            String rank,
            String specializationCategory
    );
}
