package com.ticket.moviebooking.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatHoldRequest {
    String userId;
    String scheduleId;
    String seatId;
}
