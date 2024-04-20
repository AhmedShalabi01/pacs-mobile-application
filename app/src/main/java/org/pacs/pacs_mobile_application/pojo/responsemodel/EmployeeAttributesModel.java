package org.pacs.pacs_mobile_application.pojo.responsemodel;


public class EmployeeAttributesModel {

    private String clearanceLevel;
    private String department;
    private String employmentStatus;
    private String id;
    private String role;
    private TimeSchedule timeSchedule;
    private int yearsOfExperience;

    public EmployeeAttributesModel(String clearanceLevel, String department, String employmentStatus, String id, String role, TimeSchedule timeSchedule, int yearsOfExperience) {
        this.clearanceLevel = clearanceLevel;
        this.department = department;
        this.employmentStatus = employmentStatus;
        this.id = id;
        this.role = role;
        this.timeSchedule = timeSchedule;
        this.yearsOfExperience = yearsOfExperience;
    }
}
