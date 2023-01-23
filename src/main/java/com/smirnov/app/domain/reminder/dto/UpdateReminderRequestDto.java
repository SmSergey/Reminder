package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReminderRequestDto {

    @NotNull
    private Long id;

    @Length(max = 255)
    private String title;

    @Length(max = 4096)
    private String description;

    private LocalDateTime remind;
}
