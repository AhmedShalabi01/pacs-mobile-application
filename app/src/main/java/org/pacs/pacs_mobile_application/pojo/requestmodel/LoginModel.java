package org.pacs.pacs_mobile_application.pojo.requestmodel;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginModel {

    private String email;
    private String password;

}
