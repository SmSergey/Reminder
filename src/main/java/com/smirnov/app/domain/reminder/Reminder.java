package com.smirnov.app.domain.reminder;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Setter
    private Long userId;
}
