package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.enums.AgeRating;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    String id;
    String description;
    String name;
    Integer doubleSeats;
    Integer singleSeats;
    Integer totalSeats;
}
