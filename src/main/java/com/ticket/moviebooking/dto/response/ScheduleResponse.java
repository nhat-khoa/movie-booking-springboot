package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.entity.Movie;
import com.ticket.moviebooking.entity.Room;
import com.ticket.moviebooking.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleResponse {
    String id;
    LocalDateTime startTime;
    MovieResponse movie;
    RoomResponse room;
}
