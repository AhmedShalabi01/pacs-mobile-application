package org.pacs.pacs_mobile_application.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValidationPattern {
    ID("^[a-zA-Z0-9]{1,}$","Employee ID not following the standard"),
    FIRST_NAME("^[a-zA-Z]{2,20}$", "First name must include letters only and be between 2 and 20 characters"),
    LAST_NAME("^[a-zA-Z]{2,20}$", "Last name must include letters only and be between 2 and 20 characters"),
    SSN("^\\d{3}-\\d{2}-\\d{4}$", "SSN must be in the format XXX-XX-XXXX"),
    EMAIL("[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+", "Please enter a valid email address"),
    PASSWORD("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$", "Password must contain at least one number, one capital letter, and one special character");

    private final String regex;
    private final String errorMessage;


}
