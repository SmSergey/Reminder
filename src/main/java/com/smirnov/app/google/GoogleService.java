package com.smirnov.app.google;

import org.springframework.http.HttpHeaders;

public class GoogleService {

    public static HttpHeaders getGoogleAuthorizeHeaders(String tokenValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);

        return headers;
    }
}
