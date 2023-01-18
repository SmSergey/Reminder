package com.smirnov.app.domain.reminder;

import com.smirnov.app.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Page<Reminder> findByOwner(User owner, Pageable request);

    @Query(value = "select r from Reminder r " +
            "where " +
            "(r.remind between :fromDate and :toDate) and cast(r.remind as TIME) between :fromTime and :toTime " +
            "and r.owner.id = :ownerId")
    List<Reminder> findRemindersFilteredByDateAndTime(Long ownerId, LocalDateTime fromDate, LocalDateTime toDate, LocalTime fromTime, LocalTime toTime);


}