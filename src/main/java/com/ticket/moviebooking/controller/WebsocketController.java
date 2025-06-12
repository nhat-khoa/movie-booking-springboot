package com.ticket.moviebooking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.dto.request.SeatHoldRequest;
import com.ticket.moviebooking.dto.response.SeatHoldResponse;
import com.ticket.moviebooking.service.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class WebsocketController {

    RedisService redisService;
    SimpMessagingTemplate messagingTemplate;
    ObjectMapper objectMapper;

    @MessageMapping("/select-seat")
    public void handleSeatSelection(SeatHoldRequest seatHoldRequest) throws JsonProcessingException {
        if(seatHoldRequest.getSeatId()!=null){
            redisService.saveSeatHold(seatHoldRequest);
        }

        List<SeatHoldResponse> seatsSelected =
                redisService.getSeatHoldsByScheduleId(seatHoldRequest.getScheduleId());

        // 3. Gửi broadcast đến topic theo scheduleId
        messagingTemplate.convertAndSend(
                "/topic/scheduleId/" + seatHoldRequest.getScheduleId(),
                seatsSelected
        );
    }

    @MessageMapping("/deselect-seat")
    public void handleSeatDeselection(SeatHoldRequest seatHoldRequest) throws JsonProcessingException {

        if(seatHoldRequest.getSeatId() != null){
            redisService.removeSeatHold(seatHoldRequest);
        }else{
            redisService.removeSeatHoldsByScheduleIdAndUserId(
                    seatHoldRequest.getScheduleId(),
                    seatHoldRequest.getUserId());
        }

        List<SeatHoldResponse> seatsSelected =
                redisService.getSeatHoldsByScheduleId(seatHoldRequest.getScheduleId());

        // 3. Gửi broadcast đến topic theo scheduleId
        messagingTemplate.convertAndSend(
                "/topic/scheduleId/" + seatHoldRequest.getScheduleId(),
                seatsSelected
        );
    }
}

