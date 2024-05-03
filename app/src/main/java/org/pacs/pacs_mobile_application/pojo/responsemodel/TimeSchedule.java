package org.pacs.pacs_mobile_application.pojo.responsemodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class TimeSchedule {

    private List<String> daysOfWeek;
    private String endTime;
    private String startTime;

}
