package dev.csmacf.dto;

import dev.csmacf.model.Student.ScheduleType;

public class CourseDTO {
    private Long id;
    private String name;
    private ScheduleType scheduleType;
    private int capacity;
    private int currentEnrollment;
    private String description;
    private String room;

    public CourseDTO(Long id, String name, ScheduleType scheduleType, int capacity, int currentEnrollment, String description, String room) {
        this.id = id;
        this.name = name;
        this.scheduleType = scheduleType;
        this.capacity = capacity;
        this.currentEnrollment = currentEnrollment;
        this.description = description;
        this.room = room;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ScheduleType getScheduleType() { return scheduleType; }
    public void setScheduleType(ScheduleType scheduleType) { this.scheduleType = scheduleType; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public int getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(int currentEnrollment) { this.currentEnrollment = currentEnrollment; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}
