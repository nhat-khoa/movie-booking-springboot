package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.entity.Schedule;
import com.ticket.moviebooking.enums.AgeRating;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {
    String id;
    String title;
    String titleEnglish;
    LocalDate releaseDate;
    Integer duration;
    Locale language;
    String director;
    String category;
    String posterUrl;
    String description;
    String cast;
    String trailerVideoUrl;
    AgeRating ageRating;
}
