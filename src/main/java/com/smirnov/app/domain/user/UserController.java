package com.smirnov.app.domain.user;

import com.smirnov.app.notification.telegram.bot.config.ReminderBotConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.text.MessageFormat;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ReminderBotConfiguration reminderBotConfiguration;

    @GetMapping("/account/telegram/")
    public RedirectView linkTelegram(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                                     OAuth2AuthenticationToken token) {
        final String email = token.getPrincipal().getAttribute("email");

        User owner = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));
        return new RedirectView(MessageFormat.format("https://t.me/{0}?start={1}", reminderBotConfiguration.getUsername(), owner.getId()));
    }
}
