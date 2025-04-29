package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.enums.AgeRating;
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
    String description;
    LocalDate releaseDate;
    Duration duration;
    Locale language;
    String director;
    List<String> cast;
    String trailerVideoUrl;
    AgeRating ageRating;
    String nameCategory;
}
