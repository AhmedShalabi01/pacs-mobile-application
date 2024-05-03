package org.pacs.pacs_mobile_application.pojo.responsemodel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoModel {
    private String id;
    private String ssn;
    private String firstName;
    private String lastName;
    private String email;

}
