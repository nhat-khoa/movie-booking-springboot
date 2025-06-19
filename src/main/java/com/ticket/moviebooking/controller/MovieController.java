package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.response.MovieResponse;
import com.ticket.moviebooking.service.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {
    MovieService movieService;

    @GetMapping("/{movieId}")
    ApiResponse<MovieResponse> getMovieById(@PathVariable("movieId") String movieId) {
        return ApiResponse.<MovieResponse>builder()
                .result(movieService.getMovieById(movieId))
                .build();
    }

    @GetMapping("/not-in-schedule")
    public ApiResponse<List<MovieResponse>> getMoviesNotInSchedule() {
        return ApiResponse.<List<MovieResponse>>builder()
                .result(movieService.getMoviesNotInSchedule())
                .build();
    }

    @GetMapping("/in-schedule")
    public ApiResponse<List<MovieResponse>> getMoviesInSchedule() {
        return ApiResponse.<List<MovieResponse>>builder()
                .result(movieService.getMoviesInSchedule())
                .build();
    }
}
