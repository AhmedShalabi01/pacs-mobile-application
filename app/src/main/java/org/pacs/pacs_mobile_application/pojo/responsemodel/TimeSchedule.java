package org.pacs.pacs_mobile_application.pojo.responsemodel;

import java.util.List;


public class TimeSchedule {

    private List<String> daysOfWeek;
    private String endTime;
    private String startTime;

    public TimeSchedule(List<String> daysOfWeek, String endTime, String startTime) {
        this.daysOfWeek = daysOfWeek;
        this.endTime = endTime;
        this.startTime = startTime;
    }
}
