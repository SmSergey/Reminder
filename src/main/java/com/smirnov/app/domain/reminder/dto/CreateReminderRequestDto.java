package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Validated
public class CreateReminderRequestDto {

    @NotBlank
    @Length(max = 255)
    private String title;

    @NotBlank
    @Length(max = 4096)
    private String description;

    @NotBlank
    private LocalDateTime remind;
}
