package dev.csmacf.service;

import dev.csmacf.dto.CourseDTO;
import dev.csmacf.dto.StudentScheduleDTO;
import dev.csmacf.model.Course;
import dev.csmacf.model.Student;
import dev.csmacf.repository.CourseRepository;
import dev.csmacf.repository.StudentRepository;
import dev.csmacf.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class StudentService {
 private StudentRepository studentRepository;

 private CourseRepository courseRepository;

 private CourseService courseService;

 public StudentService(StudentRepository studentRepository, CourseRepository courseRepository, CourseService courseService) {
   this.studentRepository = studentRepository;
   this.courseRepository = courseRepository;
   this.courseService = courseService;
 }

 public List<Student> getAllStudents() {
   return this.studentRepository.findAll();
 }

 public StudentScheduleDTO enrollStudent(Long studentId, Long courseId) {
   Student student = (Student)this.studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
   Course newCourse = (Course)this.courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Course not found"));
   if (newCourse.getScheduleType() == Student.ScheduleType.ALL_DAY) {
     if (student.getAmCourse() != null || student.getPmCourse() != null)
       throw new IllegalStateException("Cannot enroll in ALL_DAY course when enrolled in AM or PM courses");
     student.setAllDayCourse(newCourse);
     student.setAmCourse(null);
     student.setPmCourse(null);
   } else {
     if (student.getAllDayCourse() != null)
       throw new IllegalStateException("Cannot enroll in AM/PM course when enrolled in ALL_DAY course");
     if (newCourse.getScheduleType() == Student.ScheduleType.AM) {
       student.setAmCourse(newCourse);
     } else {
       student.setPmCourse(newCourse);
     }
   }
   student = (Student)this.studentRepository.save(student);
   return new StudentScheduleDTO(student
       .getId(), student
       .getName(), student
       .getEmail(),
       convertToDTO(student.getAmCourse()),
       convertToDTO(student.getPmCourse()),
       convertToDTO(student.getAllDayCourse()));
 }

 public Optional<Student> findBySchoolId(Integer schoolId) {
   return this.studentRepository.findBySchoolId(schoolId);
 }

 public void deleteStudentById(Long id) {
   this.studentRepository.deleteById(id);
 }

 public List<CourseDTO> getAvailableCoursesForStudent(Long studentId) {
   Student student = (Student)this.studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
   if (student.getAllDayCourse() != null)
     return (List<CourseDTO>)this.courseRepository.findByScheduleTypeAndCapacityGreaterThan(Student.ScheduleType.ALL_DAY, 0)
      
       .stream()
       .map(this::convertToDTO)
       .collect(Collectors.toList());
   if (student.getAmCourse() != null)
     return (List<CourseDTO>)this.courseService.getAvailableCoursesByType(Student.ScheduleType.PM)
       .stream()
       .map(this::convertToDTO)
       .collect(Collectors.toList());
   if (student.getPmCourse() != null)
     return (List<CourseDTO>)this.courseService.getAvailableCoursesByType(Student.ScheduleType.AM)
       .stream()
       .map(this::convertToDTO)
       .collect(Collectors.toList());
   return (List<CourseDTO>)this.courseService.getAvailableCourses()
     .stream()
     .map(this::convertToDTO)
     .collect(Collectors.toList());
 }

 public List<String> processEnrollmentCSV(MultipartFile file) throws IOException {
   List<String> results = new ArrayList<>();
   BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
   try {
     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(new String[0]));
     try {
       for (CSVRecord record : csvParser) {
         try {
           Long studentId = Long.valueOf(Long.parseLong(record.get("student_id")));
           Long courseId = Long.valueOf(Long.parseLong(record.get("course_id")));
           enrollStudent(studentId, courseId);
           results.add(String.format("Successfully enrolled student %d in course %d", new Object[] { studentId, courseId }));
         } catch (Exception e) {
           results.add(String.format("Failed to enroll student from row %d: %s", new Object[] { Long.valueOf(record.getRecordNumber()), e.getMessage() }));
         }
       }
       csvParser.close();
     } catch (Throwable throwable) {
       try {
         csvParser.close();
       } catch (Throwable throwable1) {
         throwable.addSuppressed(throwable1);
       }
       throw throwable;
     }
     reader.close();
   } catch (Throwable throwable) {
     try {
       reader.close();
     } catch (Throwable throwable1) {
       throwable.addSuppressed(throwable1);
     }
     throw throwable;
   }
   return results;
 }

 private CourseDTO convertToDTO(Course course) {
   if (course == null)
     return null;
   return new CourseDTO(course
       .getId(), course
       .getName(), course
       .getScheduleType(), course
       .getCapacity(), course
       .getCurrentEnrollment(), course
       .getDescription(), course
       .getRoom());
 }

 public Student createStudent(Student student) {
   if (student.getAllDayCourse() != null && (student
     .getAmCourse() != null || student.getPmCourse() != null))
     throw new IllegalStateException("Student cannot be enrolled in ALL_DAY and AM/PM courses simultaneously");
   return (Student)this.studentRepository.save(student);
 }

 public Optional<Student> findByEmail(String email) {
   return this.studentRepository.findByEmail(email);
 }



 public void removeFromCourse(Long studentId, String sessionType) {
   Student student = (Student)this.studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
   switch (sessionType) {
     case "AM":
       student.setAmCourse(null);
       break;
     case "PM":
       student.setPmCourse(null);
       break;
     case "ALL_DAY":
       student.setAllDayCourse(null);
       break;
   }
   this.studentRepository.save(student);
 }

 public void enrollInCourse(Long studentId, Long courseId, String sessionType) {
   Student student = (Student)this.studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException("Student not found"));
   Course course = (Course)this.courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Course not found"));
   if (!course.getScheduleType().name().equals(sessionType))
     throw new IllegalArgumentException("Course schedule type does not match requested session");
   switch (sessionType) {
     case "AM":
       student.setAmCourse(course);
       break;
     case "PM":
       student.setPmCourse(course);
       break;
     case "ALL_DAY":
       student.setAllDayCourse(course);
       break;
   }
   this.studentRepository.save(student);
 }

 public void saveStudent(Student student) {
   this.studentRepository.save(student);
 }
}


