package com.smirnov.app.google;

import org.springframework.http.HttpHeaders;

public class GoogleHelper {

    public static class Endpoints {
        public static final String GET_USER_PHONE_NUMBER = "https://people.googleapis.com/v1/people/me?personFields=phoneNumbers";
    }

    public static HttpHeaders getGoogleAuthorizeHeaders(String tokenValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenValue);

        return headers;
    }
}
