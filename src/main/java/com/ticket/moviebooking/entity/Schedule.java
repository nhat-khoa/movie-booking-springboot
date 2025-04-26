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
@Table(
    name = "schedule",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "start_time"})
    }
)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
