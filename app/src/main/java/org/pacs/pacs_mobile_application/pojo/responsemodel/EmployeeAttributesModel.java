package org.pacs.pacs_mobile_application.pojo.responsemodel;



import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeAttributesModel {
    @SerializedName("ID")
    private String id;
    @SerializedName("RL")
    private String role;
    @SerializedName("CL")
    private String clearanceLevel;
    @SerializedName("DP")
    private String department;
    @SerializedName("ES")
    private String employmentStatus;
    @SerializedName("TS")
    private TimeSchedule timeSchedule;


}
