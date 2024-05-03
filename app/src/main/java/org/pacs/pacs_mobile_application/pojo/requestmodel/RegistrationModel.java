package org.pacs.pacs_mobile_application.pojo.requestmodel;

import lombok.Getter;

@Getter
public class RegistrationModel {

    private String id;
    private String firstName;
    private String lastName;
    private String ssn;
    private String email;
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
