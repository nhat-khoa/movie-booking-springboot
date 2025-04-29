package com.ticket.moviebooking.service;

import com.ticket.moviebooking.dto.response.MovieResponse;
import com.ticket.moviebooking.exception.AppException;
import com.ticket.moviebooking.exception.ErrorCode;
import com.ticket.moviebooking.mapper.MovieMapper;
import com.ticket.moviebooking.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    MovieRepository movieRepository;
    MovieMapper movieMapper;

    public MovieResponse getMovieById(String movieId) {
        return movieMapper.toMovieResponse(
                movieRepository.findById(movieId)
                        .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED)));
    }
}
