package dev.csmacf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.csmacf.model.Course;
import dev.csmacf.model.Teacher;
import dev.csmacf.repository.CourseRepository;
import dev.csmacf.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    public TeacherService(TeacherRepository teacherRepository, CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    public Teacher createTeacher(Teacher teacher) {
        // Check if email is already in use
        if (teacherRepository.findAll().stream()
                .anyMatch(t -> t.getEmail().equals(teacher.getEmail()))) {
            throw new IllegalStateException("Email already in use");
        }
        return teacherRepository.save(teacher);
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = getTeacherById(id);
        
        // Check if new email is already in use by another teacher
        if (!teacher.getEmail().equals(teacherDetails.getEmail()) &&
            teacherRepository.findAll().stream()
                .anyMatch(t -> t.getEmail().equals(teacherDetails.getEmail()))) {
            throw new IllegalStateException("Email already in use");
        }
        
        teacher.setName(teacherDetails.getName());
        teacher.setEmail(teacherDetails.getEmail());
        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherById(id);
        if (!teacher.getCourses().isEmpty()) {
            throw new IllegalStateException("Cannot delete teacher assigned to courses");
        }
        teacherRepository.delete(teacher);
    }

    public List<Teacher> getTeachersByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        return teacherRepository.findByCourses(course);
    }

    public List<Teacher> getAvailableTeachers(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));
        return teacherRepository.findByCoursesNotContaining(course);
    }



    public Optional<Teacher> findByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    public Teacher save(Teacher newTeacher) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

  
}

