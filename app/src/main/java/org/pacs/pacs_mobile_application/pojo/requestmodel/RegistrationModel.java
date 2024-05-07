package org.pacs.pacs_mobile_application.pojo.requestmodel;


import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class RegistrationModel {
    @SerializedName("ID")
    private String id;
    @SerializedName("FN")
    private String firstName;
    @SerializedName("LN")
    private String lastName;
    @SerializedName("SSN")
    private String ssn;
    @SerializedName("EM")
    private String email;
    @SerializedName("PW")
    private String password;

    public RegistrationModel(String id, String firstName, String lastName, String ssn, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.email = email;
        this.password = password;
    }

    public RegistrationModel(String firstName, String lastName, String ssn, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
        this.email = email;
        this.password = password;
    }
}
