package com.smirnov.app.domain.reminder;

import java.time.LocalDateTime;

public class ReminderViews {

    public static class Public {
        private String title;
        private String description;
        private LocalDateTime remind;
    }
}
