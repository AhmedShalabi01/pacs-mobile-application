package org.pacs.pacs_mobile_application.pojo.responsemodel;

public class UserInfoModel {
    private String id;
    private String ssn;
    private String firstName;
    private String lastName;
    private String email;

    public UserInfoModel(String id, String ssn, String firstName, String lastName, String email) {
        this.id = id;
        this.ssn = ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getSsn() {
        return ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
