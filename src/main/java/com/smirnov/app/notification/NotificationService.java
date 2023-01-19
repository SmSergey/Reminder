package com.smirnov.app.notification;

import com.smirnov.app.domain.reminder.Reminder;
import com.smirnov.app.domain.user.User;

public interface NotificationService {
    void sendNotification(User owner, Reminder reminder);
}
