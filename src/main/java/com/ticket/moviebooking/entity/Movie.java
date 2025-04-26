package com.ticket.moviebooking.entity;

import com.ticket.moviebooking.enums.AgeRating;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String title;
    String description;
    LocalDate releaseDate;
    Duration duration;
    Locale language;
    String director;
    List<String> cast;

    @Column(length = 500)
    String trailerVideoUrl;

    @Enumerated(EnumType.STRING)
    AgeRating ageRating;

    @OneToMany(mappedBy = "movie")
    Set<MovieCategory> movieCategories;

    @OneToMany(mappedBy = "movie")
    List<Schedule> schedules;
}
