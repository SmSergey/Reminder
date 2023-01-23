package com.smirnov.app.domain.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class CreateReminderRequestDto {

    @NotBlank
    @Length(max = 255)
    private String title;

    @NotBlank
    @Size(max = 4096)
    private String description;

    @NotNull
    private LocalDateTime remind;
}
