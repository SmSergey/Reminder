package com.smirnov.app.domain.reminder;


import com.fasterxml.jackson.annotation.JsonView;
import com.smirnov.app.domain.reminder.dto.UpdateReminderRequestDto;
import com.smirnov.app.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Entity
@NamedEntityGraph(
        name = "owner-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("owner"),
        }
)
@Table(name = "reminders")
@AllArgsConstructor @NoArgsConstructor
public class Reminder {

    @Id
    @JsonView(ReminderViews.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter()
    @JsonView(ReminderViews.Public.class)
    private String title;

    @Setter()
    @JsonView(ReminderViews.Public.class)
    private String description;

    @Setter()
    @JsonView(ReminderViews.Public.class)
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
