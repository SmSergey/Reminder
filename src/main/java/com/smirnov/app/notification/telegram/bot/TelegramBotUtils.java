package com.smirnov.app.notification.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Message;

class TelegramBotUtils {

    public static String fetchUserName(Message requestMessage) {
        return requestMessage.getFrom().getUserName();
    }

    public static Long fetchInitialParameter(Message initialMessage) {
        String[] message = initialMessage.getText().split(" ");
        if (message.length != 2)
            throw new IllegalArgumentException("Invalid /start call");
        return Long.valueOf(message[1]);
    }

}
