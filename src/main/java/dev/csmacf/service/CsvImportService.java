package dev.csmacf.service;

import dev.csmacf.model.Course;
import dev.csmacf.model.Student;
import dev.csmacf.model.Teacher;
import dev.csmacf.repository.StudentRepository;
import dev.csmacf.service.CourseService;
import dev.csmacf.service.StudentService;
import dev.csmacf.service.TeacherService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CsvImportService {
 private final CourseService courseService;

 private final TeacherService teacherService;

 private final StudentService studentService;

 private StudentRepository studentRepository;

 public CsvImportService(CourseService courseService, TeacherService teacherService, StudentService studentService, StudentRepository studentRepository) {
   this.courseService = courseService;
   this.teacherService = teacherService;
   this.studentService = studentService;
   this.studentRepository = studentRepository;
 }

 public List<String> processCoursesCsv(MultipartFile file) throws Exception {
   List<String> results = new ArrayList<>();
   BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
   try {
     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim());
     try {
       for (CSVRecord record : csvParser) {
         try {
           Course course = new Course();
           course.setId(Long.valueOf(Long.parseLong(record.get("Id"))));
           course.setName(record.get("Name"));
           course.setRoom(record.get("Room"));
           String description = (record.get("Description").length() > 1000) ? record.get("Description").substring(0, 1000) : record.get("Description");
           try {
             course.setDescription(description);
           } catch (Exception e) {
             System.err.println(e);
           }
           course.setScheduleType(Student.ScheduleType.valueOf(record.get("ScheduleType")));
           course.setCapacity(Integer.parseInt(record.get("Capacity")));
           List<Teacher> teachers = new ArrayList<>();
           addTeacherIfPresent(record, "Teacher1Email", teachers);
           addTeacherIfPresent(record, "Teacher2Email", teachers);
           addTeacherIfPresent(record, "Teacher3Email", teachers);
           addTeacherIfPresent(record, "Teacher4Email", teachers);
           course.setTeachers(teachers);
           this.courseService
             .saveCourse(course);
           results.add("Course " + course.getName() + " processed successfully.");
         } catch (Exception e) {
           results.add("Error processing record: " + record.toString() + " - " + e.getMessage());
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

 private void addTeacherIfPresent(CSVRecord record, String columnName, List<Teacher> teachers) {
   String email = record.get(columnName);
   if (email != null && !email.isEmpty())
     teachers.add(getOrCreateTeacher(email));
 }

 private Teacher getOrCreateTeacher(String email) {
   return this.teacherService.findByEmail(email).orElseGet(() -> {
         Teacher newTeacher = new Teacher();
         newTeacher.setEmail(email);
         return this.teacherService.save(newTeacher);
       });
 }

 public List<String> processStudentsCsv(MultipartFile file) throws IOException {
   List<String> results = new ArrayList<>();
   BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
   try {
     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim());
     try {
       for (CSVRecord record : csvParser) {
         try {
           Student student;
           Integer studentId = Integer.valueOf(Integer.parseInt(record.get("StudentId")));
           String name = record.get("Name");
           String email = record.get("Email");
           String house = record.get("House");
           Integer grade = Integer.valueOf(Integer.parseInt(record.get("Grade")));
           Long amCourseId = (record.isSet("AMCourseId") && !record.get("AMCourseId").isEmpty()) ? Long.valueOf(Long.parseLong(record.get("AMCourseId"))) : null;
           Long pmCourseId = (record.isSet("PMCourseId") && !record.get("PMCourseId").isEmpty()) ? Long.valueOf(Long.parseLong(record.get("PMCourseId"))) : null;
           Long allDayCourseId = (record.isSet("AllDayCourseId") && !record.get("AllDayCourseId").isEmpty()) ? Long.valueOf(Long.parseLong(record.get("AllDayCourseId"))) : null;
           Optional<Student> existingStudentOpt = this.studentRepository.findBySchoolId(studentId);
           if (existingStudentOpt.isPresent()) {
             student = existingStudentOpt.get();
             student.setName(name);
             student.setEmail(email);
             student.setHouse(house);
             student.setGrade(grade.intValue());
             student.setAmCourse(null);
             student.setPmCourse(null);
             student.setAllDayCourse(null);
             if (allDayCourseId != null) {
               Course allDayCourse = (Course)this.courseService.findById(allDayCourseId).orElseThrow(() -> new IllegalArgumentException("ALL_DAY course not found: " + allDayCourseId));
               student.setAllDayCourse(allDayCourse);
             } else {
               if (amCourseId != null) {
                 Course amCourse = (Course)this.courseService.findById(amCourseId).orElseThrow(() -> new IllegalArgumentException("AM course not found: " + amCourseId));
                 student.setAmCourse(amCourse);
               }
               if (pmCourseId != null) {
                 Course pmCourse = (Course)this.courseService.findById(pmCourseId).orElseThrow(() -> new IllegalArgumentException("PM course not found: " + pmCourseId));
                 student.setPmCourse(pmCourse);
               }
             }
             results.add("Student " + name + " updated successfully");
           } else {
             student = new Student();
             student.setSchoolId(studentId);
             student.setName(name);
             student.setEmail(email);
             student.setHouse(house);
             student.setGrade(grade.intValue());
             if (allDayCourseId != null) {
               Course allDayCourse = (Course)this.courseService.findById(allDayCourseId).orElseThrow(() -> new IllegalArgumentException("ALL_DAY course not found: " + allDayCourseId));
               student.setAllDayCourse(allDayCourse);
             } else {
               if (amCourseId != null) {
                 Course amCourse = (Course)this.courseService.findById(amCourseId).orElseThrow(() -> new IllegalArgumentException("AM course not found: " + amCourseId));
                 student.setAmCourse(amCourse);
               }
               if (pmCourseId != null) {
                 Course pmCourse = (Course)this.courseService.findById(pmCourseId).orElseThrow(() -> new IllegalArgumentException("PM course not found: " + pmCourseId));
                 student.setPmCourse(pmCourse);
               }
             }
             results.add("Student " + name + " created successfully");
           }
           this.studentService.saveStudent(student);
         } catch (Exception e) {
           results.add("Error processing record: " + record.toString() + " - " + e.getMessage());
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
}





