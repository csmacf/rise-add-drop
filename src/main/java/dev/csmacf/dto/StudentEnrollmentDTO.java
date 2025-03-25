package dev.csmacf.dto;

public class StudentEnrollmentDTO {
    private Long studentId;
    private Long courseId;

    // No-args constructor for JSON deserialization
    public StudentEnrollmentDTO() {
    }

    // Full constructor
    public StudentEnrollmentDTO(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

}
