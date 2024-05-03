package org.pacs.pacs_mobile_application.pojo.responsemodel.errormodel;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ErrorBody {
    private LocalDateTime dateTime;
    @SerializedName("status")
    private  Integer status;
    @SerializedName("message")
    private String errorMessages;

}
