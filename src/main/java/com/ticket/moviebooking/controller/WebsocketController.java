package com.ticket.moviebooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.dto.request.SeatSelected;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WebsocketController {
    @Autowired
    private ObjectMapper objectMapper;

    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/select-seat")
    public void handleSeatSelection(SeatSelected seat) {
        log.info("scheduleId: {}", seat.getScheduleId());
        seat.getSeatIds().forEach(item -> {
            log.info("seatId: {}", item);
        });

        // Broadcast tới những client đã sub topic này
        messagingTemplate.convertAndSend(
                "/topic/scheduleId/" + seat.getScheduleId(),
                seat
        );
    }
}
