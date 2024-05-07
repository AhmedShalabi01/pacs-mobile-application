package org.pacs.pacs_mobile_application.pojo.responsemodel;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoModel {
    @SerializedName("ID")
    private String id;
    @SerializedName("SSN")
    private String ssn;
    @SerializedName("FN")
    private String firstName;
    @SerializedName("LN")
    private String lastName;
    @SerializedName("EM")
    private String email;

}
