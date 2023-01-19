package com.smirnov.app.notification.telegram.bot.config;

import com.smirnov.app.domain.reminder.Reminder;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

public class TelegramNotificationMessageConfig {

    public static class Messages {
        public static final String BASE_NOTIFICATION_MESSAGE_TEMPLATE =
                "====================== Notification! ===================\n" +
                        "Description: {0}\n" +
                "========================================================\n";
        public static final String ALREADY_LINKED = "Your Reminder account already linked.";
        public static final String SUCCESS_LINK = "Hello {0}, since now you will be notified with your reminders!";
        public static final String REQUIRES_AUTH = "Authenticate through [Reminder]({0}/account/telegram) for receive reminders";
    }


    public static String formatReminder(Reminder reminder) {
        return MessageFormat.format(Messages.BASE_NOTIFICATION_MESSAGE_TEMPLATE,
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getRemind().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public static String formatSuccessLink(String username) {
        return MessageFormat.format(Messages.SUCCESS_LINK, username);
    }

    public static String formatRequiresAuthMessage(String url) {
        return MessageFormat.format(Messages.REQUIRES_AUTH,url);
    }

}
