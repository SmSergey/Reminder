package com.smirnov.app.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    private Long telegramChatId;

    public User(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }
}
