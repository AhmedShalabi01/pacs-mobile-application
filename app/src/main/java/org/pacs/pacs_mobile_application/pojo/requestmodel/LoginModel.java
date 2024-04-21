package org.pacs.pacs_mobile_application.pojo.requestmodel;



public class LoginModel {
    public String getEmail() {
        return email;
    }

    private String email;
    private String password;

    public LoginModel(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
