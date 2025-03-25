package dev.csmacf.model;

import dev.csmacf.model.Course;
import dev.csmacf.validation.ValidSchedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@ValidSchedule
@Entity
public class Student {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;

 @Column(unique = true)
 private Integer schoolId;

 @Column(unique = true)
 private String email;

 @Column
 private int grade;

 private String house;

 @ManyToOne
 private Course amCourse;

 @ManyToOne
 private Course pmCourse;

 @ManyToOne
 private Course allDayCourse;

 public Student() {}

 public Student(String name, String email) {
   this.name = name;
   this.email = email;
 }

 public Long getId() {
   return this.id;
 }

 public void setId(Long id) {
   this.id = id;
 }

 public String getName() {
   return this.name;
 }

 public void setName(String name) {
   this.name = name;
 }

 public Integer getSchoolId() {
   return this.schoolId;
 }

 public void setSchoolId(Integer schoolId) {
   this.schoolId = schoolId;
 }

 public String getEmail() {
   return this.email;
 }

 public void setEmail(String email) {
   this.email = email;
 }

 public Course getAmCourse() {
   return this.amCourse;
 }

 public void setAmCourse(Course amCourse) {
   this.amCourse = amCourse;
 }

 public Course getPmCourse() {
   return this.pmCourse;
 }

 public void setPmCourse(Course pmCourse) {
   this.pmCourse = pmCourse;
 }

 public Course getAllDayCourse() {
   return this.allDayCourse;
 }

 public void setAllDayCourse(Course allDayCourse) {
   this.allDayCourse = allDayCourse;
 }

 public int getGrade() {
   return this.grade;
 }

 public void setGrade(int grade) {
   this.grade = grade;
 }

 public String getHouse() {
   return this.house;
 }

 public void setHouse(String house) {
   this.house = house;
 }

 public boolean hasValidSchedule() {
   if (this.allDayCourse != null)
     return (this.amCourse == null && this.pmCourse == null);
   return (this.amCourse != null && this.pmCourse != null);
 }
 public enum ScheduleType {
   AM,
   PM,
   ALL_DAY;
 }
}
