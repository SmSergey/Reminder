package com.smirnov.app.domain.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetUserPhoneResponse {
    private List<UserPhone> phoneNumbers;
}
