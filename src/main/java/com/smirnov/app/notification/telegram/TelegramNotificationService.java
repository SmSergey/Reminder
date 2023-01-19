package com.smirnov.app.notification.telegram;

import com.smirnov.app.domain.reminder.Reminder;
import com.smirnov.app.domain.user.User;
import com.smirnov.app.notification.telegram.bot.config.TelegramNotificationMessageConfig;
import com.smirnov.app.notification.NotificationService;
import com.smirnov.app.notification.telegram.bot.ReminderTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {

    private final ReminderTelegramBot reminderTelegramBot;

    @Override
    public void sendNotification(User owner, Reminder reminder) {
        Long chatId = owner.getTelegramChatId();
        if (chatId == null) {
            log.info("{} not connected to telegram", owner.getEmail());
            return;
        }
        SendMessage responseMessage = SendMessage.builder()
                .chatId(chatId)
                .disableNotification(false)
                .text(TelegramNotificationMessageConfig.formatReminder(reminder))
                .build();
        try {
            reminderTelegramBot.execute(responseMessage);
        } catch (TelegramApiException exception) {
            log.error("Parameter method can not be null");
        }

    }
}
