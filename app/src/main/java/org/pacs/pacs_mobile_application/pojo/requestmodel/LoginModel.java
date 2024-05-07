package org.pacs.pacs_mobile_application.pojo.requestmodel;


import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginModel {
    @SerializedName("EM")
    private String email;
    @SerializedName("PW")
    private String password;

}
