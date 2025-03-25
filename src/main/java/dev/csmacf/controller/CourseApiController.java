package dev.csmacf.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.csmacf.dto.CourseDTO;
import dev.csmacf.model.Course;
import dev.csmacf.model.Student.ScheduleType;
import dev.csmacf.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseApiController {
    private final CourseService courseService;

    public CourseApiController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/available/{sessionType}")
    public List<CourseDTO> getAvailableCourses(@PathVariable String sessionType) {
        ScheduleType scheduleType = ScheduleType.valueOf(sessionType.toUpperCase());
        List<Course> courses = courseService.getCoursesByScheduleType(scheduleType);
        return courses.stream()
                .filter(course -> course.getCurrentEnrollment() < course.getCapacity())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
            course.getId(),
            course.getName(),
            course.getScheduleType(),
            course.getCapacity(),
            // Calculate current enrollment based on schedule type
            course.getScheduleType() == ScheduleType.AM ? course.getAmStudents().size() :
            course.getScheduleType() == ScheduleType.PM ? course.getPmStudents().size() :
            course.getAllDayStudents().size(),
            course.getDescription(),
            course.getRoom()
        );
    }
}
