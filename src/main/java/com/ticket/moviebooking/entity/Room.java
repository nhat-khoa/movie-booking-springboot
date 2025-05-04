package com.ticket.moviebooking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    Integer totalSeats;
    Integer doubleSeats;
    Integer singleSeats;
    String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    Set<Seat> seats;
}
