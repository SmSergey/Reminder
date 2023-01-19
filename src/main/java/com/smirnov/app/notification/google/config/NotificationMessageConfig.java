package com.smirnov.app.notification.google.config;

import com.smirnov.app.domain.reminder.Reminder;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

public class NotificationMessageConfig {
    public static final String NOTIFICATION_SUBJECT = "Reminder app notification";

    public static final String BASE_NOTIFICATION_MESSAGE_TEMPLATE = "====================== Notification! ===================\nTitle: {0}\nDescription: {1}\nTime: {2}\n===================================================";


    public static String formatReminder(Reminder reminder) {
        return MessageFormat.format(BASE_NOTIFICATION_MESSAGE_TEMPLATE,
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getRemind().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
