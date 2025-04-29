package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.MovieResponse;
import com.ticket.moviebooking.dto.response.ScheduleResponse;
import com.ticket.moviebooking.entity.Movie;
import com.ticket.moviebooking.entity.MovieCategory;
import com.ticket.moviebooking.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(source = "movieCategories", target = "nameCategory")
    MovieResponse toMovieResponse(Movie movie);

    default String mapCategoriesToString(Set<MovieCategory> movieCategories) {
        if (movieCategories == null) {
            return "";
        }
        return movieCategories.stream()
                .map(movieCategory -> movieCategory.getCategory().getName()) // Lấy tên từ Category
                .collect(Collectors.joining(", ")); // Ghép các tên lại bằng dấu phẩy
    }
}
