package com.smirnov.app.notification.google;

import com.smirnov.app.domain.reminder.Reminder;
import com.smirnov.app.domain.user.User;
import com.smirnov.app.notification.NotificationService;
import com.smirnov.app.notification.google.config.GmailSenderConfig;
import com.smirnov.app.notification.google.config.NotificationMessageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GmailNotificationService implements NotificationService {


    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(User owner, Reminder reminder) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(GmailSenderConfig.APPLICATION_NAME);
        mailMessage.setTo(owner.getEmail());
        mailMessage.setSubject(NotificationMessageConfig.NOTIFICATION_SUBJECT);
        mailMessage.setText(NotificationMessageConfig.formatReminder(reminder));

        mailSender.send(mailMessage);
    }
}
