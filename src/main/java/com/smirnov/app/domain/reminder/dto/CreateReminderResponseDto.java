package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CreateReminderResponseDto {

    @NotNull
    private Long id;

}
