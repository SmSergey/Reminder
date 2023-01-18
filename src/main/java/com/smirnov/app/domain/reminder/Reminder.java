package com.smirnov.app.domain.reminder;


import com.smirnov.app.domain.reminder.dto.UpdateReminderRequestDto;
import com.smirnov.app.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Entity
@Table
public class Reminder {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter()
    private String title;

    @Setter()
    private String description;

    @Setter()
    private LocalDateTime remind;

    @ManyToOne
    @Setter()
    @JoinColumn(nullable = false, name = "user_id")
    private User owner;


    public Reminder mergeWithUpdateDto(UpdateReminderRequestDto dto) {
        this.setTitle(Optional.ofNullable(dto.getTitle()).orElse(this.getTitle()));
        this.setDescription(Optional.ofNullable(dto.getDescription()).orElse(this.getDescription()));
        this.setRemind(Optional.ofNullable(dto.getRemind()).orElse(this.getRemind()));
        return this;
    }
}
