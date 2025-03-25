package dev.csmacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.csmacf.model.Course;
import dev.csmacf.model.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByCourses(Course course);
    
    // Find teachers not assigned to a specific course
    List<Teacher> findByCoursesNotContaining(Course course);

    Optional<Teacher> findByEmail(String email);

}
