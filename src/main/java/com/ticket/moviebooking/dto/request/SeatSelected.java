package com.ticket.moviebooking.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatSelected {
    String userId;
    String scheduleId;
    List<String> seatIds;
}
