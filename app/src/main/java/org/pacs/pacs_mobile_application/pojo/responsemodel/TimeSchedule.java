package org.pacs.pacs_mobile_application.pojo.responsemodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class TimeSchedule {
    @SerializedName("DW")
    private List<String> daysOfWeek;
    @SerializedName("ET")
    private String endTime;
    @SerializedName("ST")
    private String startTime;
}
