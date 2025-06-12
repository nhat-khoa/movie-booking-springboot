package com.ticket.moviebooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.SeatHoldRequest;
import com.ticket.moviebooking.dto.request.TicketRequest;
import com.ticket.moviebooking.service.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisController {
    RedisService redisService;

    @PostMapping("/hold-ticket")
    public ApiResponse<String> holdTicket(@RequestBody TicketRequest request) throws JsonProcessingException {
        request.getSeatIds().forEach(seatId -> {
            try {
                redisService.saveSeatHold(SeatHoldRequest.builder()
                        .scheduleId(request.getScheduleId())
                        .seatId(seatId)
                        .userId(request.getUserId())
                        .build());
            } catch (Exception e) {
                log.error("Error saving seat hold for scheduleId: {}, seatId: {}, userId: {}",
                        request.getScheduleId(), seatId, request.getUserId(), e);
            }
        });
        String uuid = redisService.saveTicketHold(request);
        return ApiResponse.<String>builder()
                .result(uuid)
                .build();
    }
}
