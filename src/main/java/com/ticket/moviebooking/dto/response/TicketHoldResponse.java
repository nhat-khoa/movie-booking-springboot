package com.ticket.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketHoldResponse {
    String appTransId;
    ScheduleResponse schedule;
    List<SeatResponse> seats;
    Integer totalPrice;
    Long timeToLive; // in seconds;
    String orderUrl;
}
