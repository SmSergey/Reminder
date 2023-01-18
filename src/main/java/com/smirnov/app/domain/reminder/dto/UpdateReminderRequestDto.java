package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
public class UpdateReminderRequestDto {

    @NotBlank
    private Long id;

    @Length(max = 255)
    private String title;

    @Length(max = 4096)
    private String description;

    private LocalDateTime remind;
}
