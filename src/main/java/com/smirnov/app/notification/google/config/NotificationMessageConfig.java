package com.smirnov.app.notification.google.config;

import com.smirnov.app.domain.reminder.Reminder;

import java.text.MessageFormat;

public class NotificationMessageConfig {

    public static final String BASE_NOTIFICATION_MESSAGE_TEMPLATE = "====================== Notification! ===================\nDescription: {0}\n===================================================";


    public static String formatReminder(Reminder reminder) {
        return MessageFormat.format(BASE_NOTIFICATION_MESSAGE_TEMPLATE,
                reminder.getDescription());
    }
}
