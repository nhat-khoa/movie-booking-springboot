package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.response.SeatResponse;
import com.ticket.moviebooking.service.SeatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatController {
    SeatService seatService;

    @GetMapping("/{seatId}")
    ApiResponse<SeatResponse> getSeatById(@PathVariable("seatId") String seatId) {
        return ApiResponse.<SeatResponse>builder()
                .result(seatService.findById(seatId))
                .build();
    }

    @GetMapping("/by-room-id/{roomId}")
    ApiResponse<List<SeatResponse>> getMovieByDate(@PathVariable("roomId") String roomId) {
        return ApiResponse.<List<SeatResponse>>builder()
                .result(seatService.findByRoomId(roomId))
                .build();
    }
}
