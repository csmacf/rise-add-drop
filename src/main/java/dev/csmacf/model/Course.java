package dev.csmacf.model;

import dev.csmacf.model.Student;
import dev.csmacf.model.Teacher;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Course {
 @Id
 private Long id;

 private String name;

 @Enumerated(EnumType.STRING)
 private Student.ScheduleType scheduleType;

 @Column(length = 1024)
 private String description;

 private String room;

 private int capacity;

 @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
 @JoinTable(name = "course_teachers", joinColumns = {@JoinColumn(name = "course_id")}, inverseJoinColumns = {@JoinColumn(name = "teacher_id")})
 private List<Teacher> teachers = new ArrayList<>();

 @OneToMany(mappedBy = "amCourse")
 private Set<Student> amStudents = new HashSet<>();

 @OneToMany(mappedBy = "pmCourse")
 private Set<Student> pmStudents = new HashSet<>();

 @OneToMany(mappedBy = "allDayCourse")
 private Set<Student> allDayStudents = new HashSet<>();

 public int getCurrentEnrollment() {
   switch (this.scheduleType) {
     case AM:
       return this.amStudents.size();
     case   PM:
       return this.pmStudents.size();
     case ALL_DAY:
       return this.allDayStudents.size();
   }
   return 0;
 }

 public boolean hasAvailableCapacity() {
   return (getCurrentEnrollment() < this.capacity);
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

 public Student.ScheduleType getScheduleType() {
   return this.scheduleType;
 }

 public void setScheduleType(Student.ScheduleType scheduleType) {
   this.scheduleType = scheduleType;
 }

 public int getCapacity() {
   return this.capacity;
 }

 public void setCapacity(int capacity) {
   this.capacity = capacity;
 }

 public List<Teacher> getTeachers() {
   return this.teachers;
 }

 public void setTeachers(List<Teacher> teachers) {
   if (this.teachers != null)
     this.teachers.clear();
   this.teachers = teachers;
 }

 public Set<Student> getAmStudents() {
   return this.amStudents;
 }

 public void setAmStudents(Set<Student> amStudents) {
   this.amStudents = amStudents;
 }

 public Set<Student> getPmStudents() {
   return this.pmStudents;
 }

 public void setPmStudents(Set<Student> pmStudents) {
   this.pmStudents = pmStudents;
 }

 public Set<Student> getAllDayStudents() {
   return this.allDayStudents;
 }

 public void setAllDayStudents(Set<Student> allDayStudents) {
   this.allDayStudents = allDayStudents;
 }

 public String getDescription() {
   return this.description;
 }

 public void setDescription(String description) {
   this.description = description;
 }

 public String getRoom() {
   return this.room;
 }

 public void setRoom(String room) {
   this.room = room;
 }
 public List<Student> getAllStudents() {
  List<Student> allStudents = new ArrayList<>();
  allStudents.addAll(this.amStudents);
  allStudents.addAll(this.pmStudents);
  allStudents.addAll(this.allDayStudents);
  return allStudents;
}
}
