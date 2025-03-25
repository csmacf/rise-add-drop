package dev.csmacf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.csmacf.model.Course;
import dev.csmacf.model.Student.ScheduleType;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByScheduleType(ScheduleType scheduleType);
    
    List<Course> findByScheduleTypeAndCapacityGreaterThan(ScheduleType scheduleType, int minCapacity);
    
    List<Course> findByCapacityGreaterThan(int minCapacity);

    Optional<Course> findByName(String name);
}