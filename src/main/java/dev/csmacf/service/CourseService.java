package dev.csmacf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.csmacf.model.Course;
import dev.csmacf.model.Student.ScheduleType;
import dev.csmacf.model.Teacher;
import dev.csmacf.repository.CourseRepository;
import dev.csmacf.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    public CourseService(CourseRepository courseRepository, TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public Course createCourse(Course course) {
        // Validate capacity is positive
        if (course.getCapacity() <= 0) {
            throw new IllegalArgumentException("Course capacity must be greater than 0");
        }
        return courseRepository.save(course);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    public List<Course> getAvailableCoursesByType(ScheduleType scheduleType) {
        // Get all courses of the specified type with remaining capacity
        return courseRepository.findByScheduleTypeAndCapacityGreaterThan(scheduleType, 0);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);
        course.setName(courseDetails.getName());
        course.setCapacity(courseDetails.getCapacity());
        course.setScheduleType(courseDetails.getScheduleType());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        // Check if there are any students enrolled
        if (!course.getAmStudents().isEmpty() || 
            !course.getPmStudents().isEmpty() || 
            !course.getAllDayStudents().isEmpty()) {
            throw new IllegalStateException("Cannot delete course with enrolled students");
        }
        courseRepository.delete(course);
    }

    public Course addTeacherToCourse(Long courseId, Long teacherId) {
        Course course = getCourseById(courseId);
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));
        
        course.getTeachers().add(teacher);
        return courseRepository.save(course);
    }

    public Course removeTeacherFromCourse(Long courseId, Long teacherId) {
        Course course = getCourseById(courseId);
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));
        
        if (course.getTeachers().size() <= 1) {
            throw new IllegalStateException("Cannot remove the only teacher from a course");
        }
        
        course.getTeachers().remove(teacher);
        return courseRepository.save(course);
    }

    public List<Course> getCoursesByScheduleType(ScheduleType scheduleType) {
        return courseRepository.findByScheduleType(scheduleType);
    }

    public List<Course> getAvailableCourses() {
        return courseRepository.findByCapacityGreaterThan(0);
    }

    public Optional<Course> findByName(String name) {
        return courseRepository.findByName(name);
    }


    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> findById(Long allDayCourseId) {
        return courseRepository.findById(allDayCourseId);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }
}


