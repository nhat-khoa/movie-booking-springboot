package com.ticket.moviebooking.entity;

import com.ticket.moviebooking.enums.AgeRating;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.List;

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
    String titleEnglish;
    LocalDate releaseDate;
    Integer duration;
    String language;
    String director;
    String category;

    @Column(length = 500)
    String backgroundUrl;

    @Column(length = 500)
    String posterUrl;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String cast;

    @Column(length = 500)
    String trailerVideoUrl;

    @Enumerated(EnumType.STRING)
    AgeRating ageRating;

    @OneToMany(mappedBy = "movie")
    List<Schedule> schedules;
}
