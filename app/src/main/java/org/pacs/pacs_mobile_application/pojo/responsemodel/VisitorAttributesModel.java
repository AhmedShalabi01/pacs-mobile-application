package org.pacs.pacs_mobile_application.pojo.responsemodel;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisitorAttributesModel {
    private String clearanceLevel;
    private String department;
    private String id;
    private String role;
    private TimeSchedule timeSchedule;

}
