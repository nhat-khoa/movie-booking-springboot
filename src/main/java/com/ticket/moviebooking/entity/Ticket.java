package com.ticket.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    Seat seat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    LocalDateTime reservedAt;

    String status; // reserved, paid, canceled...

    // Optional: price (giá vé cụ thể cho ghế này)
}
