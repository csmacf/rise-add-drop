package dev.csmacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.csmacf.model.Course;
import dev.csmacf.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAmCourse(Course course);
    List<Student> findByPmCourse(Course course);
    List<Student> findByAllDayCourse(Course course);
    
    Optional<Student> findBySchoolId(Integer studentId);
    
    Optional<Student> findByEmail(String email);


    
}

