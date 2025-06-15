package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.PaymentCreateRequest;
import com.ticket.moviebooking.dto.request.SeatHoldRequest;
import com.ticket.moviebooking.dto.response.PaymentCreateResponse;
import com.ticket.moviebooking.service.RedisService;
import com.ticket.moviebooking.service.ZaloPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentController {

    RedisService redisService;
    ZaloPayService zaloPayService;

    @PostMapping("/create")
    public ApiResponse<PaymentCreateResponse> create(@RequestBody PaymentCreateRequest request) throws Exception {
        Boolean isSuccess = true;
        for (String seatId : request.getSeatIds()) {
            try {
                isSuccess = redisService.setSeatHoldTTL(SeatHoldRequest.builder()
                        .scheduleId(request.getScheduleId())
                        .seatId(seatId)
                        .userId(request.getUserId())
                        .build());
                if (!isSuccess) {
                    break;
                }
            } catch (Exception e) {
                log.error("Error saving seat hold for scheduleId: {}, seatId: {}, userId: {}",
                        request.getScheduleId(), seatId, request.getUserId(), e);
                isSuccess = false;
            }
        }

        if(!isSuccess) {
            return ApiResponse.<PaymentCreateResponse>builder()
                    .result(PaymentCreateResponse.builder()
                            .returnCode(-99)
                            .returnMessage("No seat available or error saving seat hold")
                            .build())
                    .build();
        }

        PaymentCreateResponse response = zaloPayService.createPayment(request);
        redisService.saveTicketHold(response.getAppTransId(), response.getOrderUrl(), request);

        return ApiResponse.<PaymentCreateResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping("/callback")
    public Map<String, Object> callback(@RequestBody String jsonStr){
        log.info("Received callback with data: {}", jsonStr);
        return zaloPayService.handleCallback(jsonStr);
    }
}
