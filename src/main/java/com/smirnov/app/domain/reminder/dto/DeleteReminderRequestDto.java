package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class DeleteReminderRequestDto {

    @NotBlank
    private Long id;
}
