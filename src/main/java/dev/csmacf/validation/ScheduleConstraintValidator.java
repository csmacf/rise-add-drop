package dev.csmacf.validation;

import dev.csmacf.model.Student;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ScheduleConstraintValidator implements ConstraintValidator<ValidSchedule, Student> {
    @Override
    
        public boolean isValid(Student student, ConstraintValidatorContext context) {
            // Allow students with no courses
            if (student.getAmCourse() == null && student.getPmCourse() == null && student.getAllDayCourse() == null) {
                return true;
            }
    
            // Allow students with just AM or just PM course
            if (student.getAllDayCourse() == null) {
                return true; // Allow any combination of AM/PM courses
            }
    
            // If student has ALL_DAY course, they can't have AM or PM courses
            return student.getAmCourse() == null && student.getPmCourse() == null;
        }
    }

