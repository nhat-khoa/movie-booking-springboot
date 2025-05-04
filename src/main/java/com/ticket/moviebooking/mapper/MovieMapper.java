package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.MovieResponse;
import com.ticket.moviebooking.entity.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieResponse toMovieResponse(Movie movie);
}
