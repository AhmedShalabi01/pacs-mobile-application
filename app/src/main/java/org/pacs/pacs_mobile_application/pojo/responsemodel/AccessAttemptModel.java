package org.pacs.pacs_mobile_application.pojo.responsemodel;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccessAttemptModel {
    @SerializedName("ID")
    private String id;
    @SerializedName("LC")
    private String location;
    @SerializedName("TA")
    private String timeAccess;
    @SerializedName("DS")
    private String decision;
}
