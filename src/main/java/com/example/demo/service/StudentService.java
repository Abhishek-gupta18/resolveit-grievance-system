package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Get all students from database
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Get student by ID
     */
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Get student by email
     */
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    /**
     * Get students by course
     */
    public List<Student> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    /**
     * Create/Save new student
     */
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    /**
     * Update existing student
     */
    public Student updateStudent(Long id, Student studentDetails) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            Student existingStudent = student.get();
            if (studentDetails.getName() != null) existingStudent.setName(studentDetails.getName());
            if (studentDetails.getEmail() != null) existingStudent.setEmail(studentDetails.getEmail());
            if (studentDetails.getPhone() != null) existingStudent.setPhone(studentDetails.getPhone());
            if (studentDetails.getAddress() != null) existingStudent.setAddress(studentDetails.getAddress());
            if (studentDetails.getCourse() != null) existingStudent.setCourse(studentDetails.getCourse());
            if (studentDetails.getGpa() != null) existingStudent.setGpa(studentDetails.getGpa());
            if (studentDetails.getEnrollmentDate() != null) existingStudent.setEnrollmentDate(studentDetails.getEnrollmentDate());
            if (studentDetails.getStatus() != null) existingStudent.setStatus(studentDetails.getStatus());
            return studentRepository.save(existingStudent);
        }
        return null;
    }

    /**
     * Delete student by ID
     */
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    /**
     * Get students by status
     */
    public List<Student> getStudentsByStatus(String status) {
        return studentRepository.findByStatus(status);
    }

    /**
     * Get high performers (GPA >= 3.5)
     */
    public List<Student> getHighPerformers() {
        return studentRepository.findByGpaGreaterThanEqual(3.5);
    }

    /**
     * Get total count of students
     */
    public long getTotalStudentCount() {
        return studentRepository.count();
    }
}
