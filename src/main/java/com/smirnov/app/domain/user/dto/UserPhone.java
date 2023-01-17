package com.smirnov.app.domain.user.dto;

import lombok.Data;

@Data
public class UserPhone {
    private String canonicalForm;
    private UserPhoneMetadata metadata;
}
