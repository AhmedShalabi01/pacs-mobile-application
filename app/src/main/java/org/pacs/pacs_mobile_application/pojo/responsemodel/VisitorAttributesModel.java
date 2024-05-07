package org.pacs.pacs_mobile_application.pojo.responsemodel;


import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisitorAttributesModel {
    @SerializedName("CL")
    private String clearanceLevel;
    @SerializedName("DP")
    private String department;
    @SerializedName("ID")
    private String id;
    @SerializedName("RL")
    private String role;
    @SerializedName("TS")
    private TimeSchedule timeSchedule;

}
