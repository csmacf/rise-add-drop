package dev.csmacf.dto;

public class StudentTransferDTO {
    private Long studentId;
    private Long fromCourseId;
    private Long toCourseId;

    // No-args constructor for JSON deserialization
    public StudentTransferDTO() {
    }

    // Full constructor
    public StudentTransferDTO(Long studentId, Long fromCourseId, Long toCourseId) {
        this.studentId = studentId;
        this.fromCourseId = fromCourseId;
        this.toCourseId = toCourseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getFromCourseId() {
        return fromCourseId;
    }

    public void setFromCourseId(Long fromCourseId) {
        this.fromCourseId = fromCourseId;
    }

    public Long getToCourseId() {
        return toCourseId;
    }

    public void setToCourseId(Long toCourseId) {
        this.toCourseId = toCourseId;
    }

}