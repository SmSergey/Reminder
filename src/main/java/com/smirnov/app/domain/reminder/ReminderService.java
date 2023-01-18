package com.smirnov.app.domain.reminder;

import com.smirnov.app.domain.reminder.dto.CreateReminderRequestDto;
import com.smirnov.app.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public Reminder createReminder(CreateReminderRequestDto dto, User owner) {

        Reminder newReminder = new Reminder();

        newReminder.setDescription(dto.getDescription());
        newReminder.setRemind(dto.getRemind());
        newReminder.setTitle(dto.getTitle());
        newReminder.setOwner(owner);

        return reminderRepository.save(newReminder);
    }
}
