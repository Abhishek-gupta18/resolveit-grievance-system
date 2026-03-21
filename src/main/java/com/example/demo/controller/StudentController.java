package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * GET: Retrieve all students
     * Endpoint: http://localhost:8008/api/students
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * GET: Retrieve student by ID
     * Endpoint: http://localhost:8008/api/students/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET: Retrieve student by email
     * Endpoint: http://localhost:8008/api/students/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email) {
        Optional<Student> student = studentService.getStudentByEmail(email);
        return student.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET: Retrieve students by course
     * Endpoint: http://localhost:8008/api/students/course/{course}
     */
    @GetMapping("/course/{course}")
    public ResponseEntity<List<Student>> getStudentsByCourse(@PathVariable String course) {
        List<Student> students = studentService.getStudentsByCourse(course);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * GET: Retrieve students by status
     * Endpoint: http://localhost:8008/api/students/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Student>> getStudentsByStatus(@PathVariable String status) {
        List<Student> students = studentService.getStudentsByStatus(status);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * GET: Get high performers (GPA >= 3.5)
     * Endpoint: http://localhost:8008/api/students/top/performers
     */
    @GetMapping("/top/performers")
    public ResponseEntity<List<Student>> getHighPerformers() {
        List<Student> students = studentService.getHighPerformers();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * GET: Get total student count
     * Endpoint: http://localhost:8008/api/students/count/total
     */
    @GetMapping("/count/total")
    public ResponseEntity<Long> getTotalStudentCount() {
        long count = studentService.getTotalStudentCount();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    /**
     * POST: Create a new student
     * Endpoint: http://localhost:8008/api/students
     * RequestBody: Student JSON object
     */
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        try {
            Student savedStudent = studentService.saveStudent(student);
            return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * PUT: Update existing student
     * Endpoint: http://localhost:8008/api/students/{id}
     * RequestBody: Updated Student JSON object
     */
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        try {
            Student updatedStudent = studentService.updateStudent(id, studentDetails);
            if (updatedStudent != null) {
                return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * DELETE: Remove a student
     * Endpoint: http://localhost:8008/api/students/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            Optional<Student> student = studentService.getStudentById(id);
            if (student.isPresent()) {
                studentService.deleteStudent(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
