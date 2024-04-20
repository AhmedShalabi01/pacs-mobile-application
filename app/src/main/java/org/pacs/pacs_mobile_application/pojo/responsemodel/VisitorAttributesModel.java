package org.pacs.pacs_mobile_application.pojo.responsemodel;




public class VisitorAttributesModel {
    private String clearanceLevel;
    private String department;
    private String id;
    private String role;
    private TimeSchedule timeSchedule;

    public VisitorAttributesModel(String clearanceLevel, String department, String id, String role, TimeSchedule timeSchedule) {
        this.clearanceLevel = clearanceLevel;
        this.department = department;
        this.id = id;
        this.role = role;
        this.timeSchedule = timeSchedule;
    }
}
