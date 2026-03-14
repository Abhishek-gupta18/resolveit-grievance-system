package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find student by email
     */
    Optional<Student> findByEmail(String email);

    /**
     * Find students by course
     */
    List<Student> findByCourse(String course);

    /**
     * Find students by status
     */
    List<Student> findByStatus(String status);

    /**
     * Find students with GPA greater than or equal to a value
     */
    List<Student> findByGpaGreaterThanEqual(Double gpa);
}
