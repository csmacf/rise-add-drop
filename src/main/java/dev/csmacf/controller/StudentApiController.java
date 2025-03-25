package dev.csmacf.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import dev.csmacf.dto.CourseDTO;
import dev.csmacf.dto.StudentScheduleDTO;
import dev.csmacf.dto.StudentTransferDTO;
import dev.csmacf.model.Student;
import dev.csmacf.service.StudentService;
import dev.csmacf.utilities.EnrollmentRequest;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {
    private final StudentService studentService;

    public StudentApiController(StudentService studentService) {
        this.
        studentService = studentService;
    }
 @GetMapping("/email-suggestions")
    public List<String> getEmailSuggestions(@RequestParam("email") String email) {
        List<Student> students = studentService.findStudentsByEmailStartingWith(email);
        return students.stream()
                .map(Student::getEmail)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enrollStudent(@PathVariable Long id, 
                                         @RequestBody EnrollmentRequest request) {
        try {
            if (request.getCourseId() == null) {
                // Handle removal from course
                studentService.removeFromCourse(id, request.getSessionType());
            } else {
                // Handle enrollment in course
                studentService.enrollInCourse(id, request.getCourseId(), request.getSessionType());
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    
    @GetMapping("/{id}/available-courses")
    public ResponseEntity<List<CourseDTO>> getAvailableCourses(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getAvailableCoursesForStudent(id));
    }

    @PostMapping("/batch-enroll")
    public ResponseEntity<List<String>> batchEnrollFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            List<String> results = studentService.processEnrollmentCSV(file);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to process CSV file");
        }
    }
}
