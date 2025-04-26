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
public class MovieCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    LocalDateTime addedAt;
    String note;

    @ManyToOne
    Movie movie;

    @ManyToOne
    Category category;

}
