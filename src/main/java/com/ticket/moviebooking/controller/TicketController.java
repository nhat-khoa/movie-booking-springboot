package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.TicketRequest;
import com.ticket.moviebooking.dto.response.TicketResponse;
import com.ticket.moviebooking.service.TicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketController {
    TicketService ticketService;

    @GetMapping("/exists-by-schedule-id-and-seat-id")
    public ApiResponse<Boolean> existsByScheduleIdAndSeatId(
            @RequestParam String scheduleId,
            @RequestParam String seatId
    ) {
        return ApiResponse.<Boolean>builder()
                .result(ticketService.existsByScheduleIdAndSeatId(scheduleId, seatId))
                .build();
    }

    @PostMapping
    public ApiResponse<TicketResponse> createTicket(@RequestBody TicketRequest request) {
        return ApiResponse.<TicketResponse>builder()
                .result(ticketService.createTicket(
                        request.getScheduleId(),
                        request.getUserId(),
                        request.getSeatIds()))
                .build();
    }
}
