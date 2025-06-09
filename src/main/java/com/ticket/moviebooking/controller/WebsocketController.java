package com.ticket.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.dto.request.SeatSelected;
import com.ticket.moviebooking.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WebsocketController {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping("/select-seat")
    public void handleSeatSelection(SeatSelected seat) {
        log.info("User '{}' is selecting seats '{}' for schedule '{}'", seat.getUserId(), seat.getSeatIds(), seat.getScheduleId());

        // 1. Cập nhật danh sách ghế đã chọn vào Redis
        redisService.updateSeat(seat);

        List<SeatSelected> seatsSelected = redisService.findByScheduleId(seat.getScheduleId());

        // 3. Gửi broadcast đến topic theo scheduleId
        messagingTemplate.convertAndSend(
                "/topic/scheduleId/" + seat.getScheduleId(),
                seatsSelected
        );
    }
}

