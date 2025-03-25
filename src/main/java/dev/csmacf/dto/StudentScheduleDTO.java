package dev.csmacf.dto;

public class StudentScheduleDTO {
    private Long studentId;
    private String name;
    
    private String email;
    private CourseDTO amCourse;
    private CourseDTO pmCourse;
    private CourseDTO allDayCourse;
    public StudentScheduleDTO() {
    }
    
    public StudentScheduleDTO(Long studentId, String name, String email, CourseDTO amCourse, CourseDTO pmCourse,
            CourseDTO allDayCourse) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.amCourse = amCourse;
        this.pmCourse = pmCourse;
        this.allDayCourse = allDayCourse;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CourseDTO getAmCourse() {
        return amCourse;
    }

    public void setAmCourse(CourseDTO amCourse) {
        this.amCourse = amCourse;
    }

    public CourseDTO getPmCourse() {
        return pmCourse;
    }

    public void setPmCourse(CourseDTO pmCourse) {
        this.pmCourse = pmCourse;
    }

    public CourseDTO getAllDayCourse() {
        return allDayCourse;
    }

    public void setAllDayCourse(CourseDTO allDayCourse) {
        this.allDayCourse = allDayCourse;
    }

 
    
}
