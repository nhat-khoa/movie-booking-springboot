package com.ticket.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatResponse {
    String id;
    String line;     // Hàng: A, B, C...
    Integer number; // Số ghế trong hàng: 1, 2, 3...
    Boolean isActive = true; // Ghế này còn sử dụng hay đã bỏ
    Boolean isDoubleSeat = false;
    String roomId;
}
