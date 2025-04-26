package com.ticket.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "seat", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"room_id", "line", "column"})
})
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String line;     // Hàng: A, B, C...
    Integer number; // Số ghế trong hàng: 1, 2, 3...
    Boolean isActive = true; // Ghế này còn sử dụng hay đã bỏ
    Boolean isDoubleSeat = false;
    // Optional: ghép row + number thành seatCode: "A5", "B10"

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    Room room;
}
