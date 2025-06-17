package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.enums.AgeRating;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    String language;
    String director;
    String category;
    String backgroundUrl;
    String posterUrl;
    String description;
    String cast;
    String trailerVideoUrl;
    AgeRating ageRating;
}
