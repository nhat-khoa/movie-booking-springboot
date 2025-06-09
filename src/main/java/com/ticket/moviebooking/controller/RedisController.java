package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.SeatRequest;
import com.ticket.moviebooking.dto.request.SeatSelected;
import com.ticket.moviebooking.service.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seat-is-being-selected")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisController {

    RedisService redisService;

    @GetMapping("/{scheduleId}")
    public ApiResponse<List<SeatSelected>> getSelectedSeats(@PathVariable String scheduleId) {
        return ApiResponse.<List<SeatSelected>>builder()
                .result(redisService.findByScheduleId(scheduleId))
                .build();
    }

    @PostMapping
    public ApiResponse<String> addSeat(@RequestBody SeatSelected seatSelected) {
        redisService.updateSeat(seatSelected);
        return ApiResponse.<String>builder()
                .result("Seat(s) added successfully")
                .build();
    }

    @DeleteMapping
    public ApiResponse<String> removeSeats(
            @RequestParam String scheduleId,
            @RequestParam String userId,
            @RequestBody List<String> seatIds) {
        redisService.removeSeats(scheduleId, userId, seatIds);
        return ApiResponse.<String>builder()
                .result("Seat(s) removed successfully")
                .build();
    }

    @GetMapping("/{scheduleId}/others/{userId}")
    public ApiResponse<List<String>> getSeatsByOthers(
            @PathVariable String scheduleId,
            @PathVariable String userId) {
        return ApiResponse.<List<String>>builder()
                .result(redisService.findSeatIdsSelectedByOthers(scheduleId, userId))
                .build();
    }
}
