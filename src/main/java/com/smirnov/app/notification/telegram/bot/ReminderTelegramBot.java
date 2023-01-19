package com.smirnov.app.notification.telegram.bot;

import com.smirnov.app.config.ApplicationConfiguration;
import com.smirnov.app.domain.user.User;
import com.smirnov.app.domain.user.UserRepository;
import com.smirnov.app.notification.telegram.bot.config.TelegramNotificationMessageConfig;
import com.smirnov.app.notification.telegram.bot.config.ReminderBotConfiguration;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.MessageFormat;

@Slf4j
@Component
public class ReminderTelegramBot extends TelegramLongPollingBot {

    private final ApplicationConfiguration applicationConfiguration;
    private final ReminderBotConfiguration botConfig;
    private final UserRepository userRepository;


    public ReminderTelegramBot(TelegramBotsApi telegramBotsApi,
                               ApplicationConfiguration applicationConfiguration, ReminderBotConfiguration reminderBotConfiguration,
                               UserRepository userRepository) throws TelegramApiException {
        this.applicationConfiguration = applicationConfiguration;
        this.userRepository = userRepository;
        this.botConfig = reminderBotConfiguration;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message requestMessage = update.getMessage();
        if (getChatStatus(update).equals(ChatStatus.KICKED)) {
            log.warn("User: {} was unsubscribe from notifications", TelegramBotUtils.fetchUserName(requestMessage));
        }
        if (requestMessage.getText().contains("/start")) {
            try {
                initialCall(requestMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialCall(Message requestMessage) throws TelegramApiException {

        final Long chatId = requestMessage.getChatId();

        if (requestMessage.getText().equals("/start")) {
            if (isChatLinked(chatId)) {
                sendMessage(requestMessage, TelegramNotificationMessageConfig.Messages.ALREADY_LINKED);
            } else {
                sendRequiresLoginResponse(requestMessage);
            }
            return;
        }

        final Long userId = TelegramBotUtils.fetchInitialParameter(requestMessage);
        linkChat(userId, chatId);

        sendMessage(requestMessage, TelegramNotificationMessageConfig.formatSuccessLink(requestMessage.getFrom().getUserName()));
    }

    private void sendMessage(Message message, String text) throws TelegramApiException {
        SendMessage responseMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(text)
                .build();
        execute(responseMessage);
    }

    private void sendRequiresLoginResponse(Message message) throws TelegramApiException {
        final String url = applicationConfiguration.getServerUrl();

        SendMessage responseMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(TelegramNotificationMessageConfig.formatRequiresAuthMessage(url))
                .parseMode(ParseMode.MARKDOWNV2)
                .build();
        execute(responseMessage);
    }

    private boolean isChatLinked(Long chatId) {
        return userRepository.existsByTelegramChatId(chatId);
    }

    private String getChatStatus(Update update) {
        if (update.getMyChatMember() == null)
            return ChatStatus.EMPTY;
        return update.getMyChatMember().getNewChatMember().getStatus();
    }

    private void linkChat(Long userId, Long chatId) {
        User userToLink = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("User with id: {0} not found", userId)));

        userToLink.setTelegramChatId(chatId);
        userRepository.save(userToLink);
    }
}
