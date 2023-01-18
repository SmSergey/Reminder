package com.smirnov.app.domain.reminder.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.smirnov.app.domain.reminder.Reminder;
import com.smirnov.app.domain.reminder.ReminderViews;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@JsonView(ReminderViews.Public.class)
public class ListRemindersResponseDto {

    @JsonView(ReminderViews.Public.class)
    private List<Reminder> content;

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private int totalElements;

    public ListRemindersResponseDto(Page<Reminder> page) {
        this.setContent(page.getContent());
        this.setPageSize(page.getSize());
        this.setCurrentPage(page.getNumber());
        this.setTotalElements(page.getNumberOfElements());
        this.setTotalPages(page.getTotalPages());
    }
}
