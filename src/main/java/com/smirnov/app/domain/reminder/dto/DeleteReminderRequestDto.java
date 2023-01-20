package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@AllArgsConstructor @NoArgsConstructor
public class DeleteReminderRequestDto {

    @NotBlank
    private Long id;
}
