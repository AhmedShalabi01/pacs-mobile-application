package org.pacs.pacs_mobile_application.pojo.responsemodel;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeAttributesModel {

    private String clearanceLevel;
    private String department;
    private String employmentStatus;
    private String id;
    private String role;
    private TimeSchedule timeSchedule;
    private int yearsOfExperience;

}
