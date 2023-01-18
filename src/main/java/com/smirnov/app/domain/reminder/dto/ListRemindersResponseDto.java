package com.smirnov.app.domain.reminder.dto;

import com.smirnov.app.domain.reminder.Reminder;
import lombok.Data;

import java.util.List;

@Data
public class ListRemindersResponseDto {

    private List<Reminder> content;

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private int totalElements;
}
