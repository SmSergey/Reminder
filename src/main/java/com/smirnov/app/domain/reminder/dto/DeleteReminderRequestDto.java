package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@AllArgsConstructor @NoArgsConstructor
public class DeleteReminderRequestDto {

    @NotNull
    private Long id;
}
