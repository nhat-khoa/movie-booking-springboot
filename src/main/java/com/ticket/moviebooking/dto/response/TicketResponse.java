package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.entity.Schedule;
import com.ticket.moviebooking.entity.TicketSeat;
import com.ticket.moviebooking.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponse {
    String id;
    LocalDateTime bookedAt;
    String status; // pending, paid
    String scheduleId;
    String userId;
}
