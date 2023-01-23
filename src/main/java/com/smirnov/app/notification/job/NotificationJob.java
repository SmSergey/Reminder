package com.smirnov.app.notification.job;

import com.smirnov.app.domain.reminder.Reminder;
import com.smirnov.app.domain.reminder.ReminderRepository;
import com.smirnov.app.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationJob implements Job {

    private final List<NotificationService> notificationServices;
    private final ReminderRepository reminderRepository;

    public void execute(JobExecutionContext context) {
        List<Reminder> remindersToExecute = reminderRepository.findAllByRemindBefore(LocalDateTime.now());
        remindersToExecute.forEach((reminder) -> {
            notificationServices.forEach((notificationService) -> notificationService.sendNotification(reminder.getOwner(), reminder));
        });
        reminderRepository.deleteAll(remindersToExecute);
    }
}
