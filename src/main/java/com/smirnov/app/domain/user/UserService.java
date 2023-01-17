package com.smirnov.app.domain.user;


import com.smirnov.app.domain.user.dto.GetUserPhoneResponse;
import com.smirnov.app.domain.user.dto.UserPhone;
import com.smirnov.app.google.GoogleConfig;
import com.smirnov.app.google.GoogleService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();
    private String number;

    public String getPhoneNumber(OAuth2AccessToken token) {
        if (number == null) {
            this.number = retrieveUserPhoneNumber(token);
        }
        return this.number;
    }

    private String retrieveUserPhoneNumber(OAuth2AccessToken token) {
        GetUserPhoneResponse response = restTemplate.exchange(
                GoogleConfig.Endpoints.GET_USER_PHONE_NUMBER,
                HttpMethod.GET,
                new HttpEntity<>(GoogleService.getGoogleAuthorizeHeaders(token.getTokenValue())),
                GetUserPhoneResponse.class
        ).getBody();

        if (response == null) {
            throw new RuntimeException("no response from google api");
        }

        UserPhone primaryUserPhone = response.getPhoneNumbers()
                .stream()
                .filter(userPhone -> userPhone.getMetadata().isPrimary())
                .findFirst()
                .orElse(new UserPhone());

        return primaryUserPhone.getCanonicalForm();
    }
}
