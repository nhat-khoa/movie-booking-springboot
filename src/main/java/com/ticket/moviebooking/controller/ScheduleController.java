package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.response.ScheduleResponse;
import com.ticket.moviebooking.service.ScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleController {
    ScheduleService scheduleService;

    @GetMapping
    ApiResponse<List<LocalDate>> getSchedules() {
        return ApiResponse.<List<LocalDate>>builder()
                .result(scheduleService.getSchedules())
                .build();
    }

    @GetMapping("/movie-by-date/{date}")
    ApiResponse<Set<String>> getMovieByDate(@PathVariable("date") LocalDate localDate) {
        return ApiResponse.<Set<String>>builder()
                .result(scheduleService.getMovieByDate(localDate))
                .build();
    }

    @GetMapping("/date/{date}")
    ApiResponse<List<ScheduleResponse>> findByStartDate(@PathVariable("date") LocalDate localDate) {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.findByStartDate(localDate))
                .build();
    }

    @GetMapping("/by-date-and-movie-id/{date}/{movieId}")
    ApiResponse<List<ScheduleResponse>> findByMovieIdAndDate(
            @PathVariable("date") LocalDate localDate,
            @PathVariable("movieId") String movieId
    ) {
        return ApiResponse.<List<ScheduleResponse>>builder()
                .result(scheduleService.findByMovieIdAndDate(movieId,localDate))
                .build();
    }

    @GetMapping("/by-id/{scheduleId}")
    ApiResponse<ScheduleResponse> getScheduleById(@PathVariable("scheduleId") String scheduleId) {
        return ApiResponse.<ScheduleResponse>builder()
                .result(scheduleService.getScheduleById(scheduleId))
                .build();
    }
}
