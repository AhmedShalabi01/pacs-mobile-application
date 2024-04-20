package org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class ErrorBody {
    private LocalDateTime dateTime;
    @SerializedName("status")
    private  Integer status;
    @SerializedName("message")
    private String errorMessages;

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessages() {
        return errorMessages;
    }
}
