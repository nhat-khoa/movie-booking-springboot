package com.ticket.moviebooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.PaymentCreateRequest;
import com.ticket.moviebooking.dto.request.SeatHoldRequest;
import com.ticket.moviebooking.dto.response.PaymentCreateResponse;
import com.ticket.moviebooking.dto.response.TicketHoldResponse;
import com.ticket.moviebooking.service.RedisService;
import com.ticket.moviebooking.service.ZaloPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisController {

    RedisService redisService;

    @GetMapping("/ticket")
    public ApiResponse<List<TicketHoldResponse>> getTicketHoldByUserId() throws JsonProcessingException {
        return ApiResponse.<List<TicketHoldResponse>>builder()
                .result(redisService.findAllTicketHoldByUserId())
                .build();
    }

}
