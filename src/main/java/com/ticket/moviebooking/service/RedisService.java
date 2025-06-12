package com.ticket.moviebooking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.dto.request.SeatHoldRequest;
import com.ticket.moviebooking.dto.request.TicketRequest;
import com.ticket.moviebooking.dto.response.SeatHoldResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisService {

    RedisTemplate<String, String> redisTemplate;
    ObjectMapper objectMapper;

    public void saveSeatHold(SeatHoldRequest seatHoldRequest) throws JsonProcessingException {
        String scheduleId = seatHoldRequest.getScheduleId();
        String seatId = seatHoldRequest.getSeatId();
        String userId = seatHoldRequest.getUserId();

        // Tạo key dạng: scheduleId:seatId:userId
        String key = String.format("seat_hold:%s:%s:%s", scheduleId, userId, seatId);

        // Tạo object và chuyển sang JSON
        Map<String, Object> value = new HashMap<>();
        value.put("schedule_id", scheduleId);
        value.put("seat_id", seatId);
        value.put("user_id", userId);
        String jsonValue = objectMapper.writeValueAsString(value);

        // Lưu vào Redis với TTL 5 phút (300 giây)
        redisTemplate.opsForValue().set(key, jsonValue, Duration.ofMinutes(5));
    }

    public List<SeatHoldResponse> getSeatHoldsByScheduleId(String scheduleId) {
        String pattern = "seat_hold:" + scheduleId + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        List<SeatHoldResponse> results = new ArrayList<>();

        if (keys == null || keys.isEmpty()) {
            return results;
        }

        for (String key : keys) {
            try {
                String json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    JsonNode node = objectMapper.readTree(json);
                    String seatId = node.get("seat_id").asText();
                    String userId = node.get("user_id").asText();
                    results.add(SeatHoldResponse.builder()
                            .seatId(seatId)
                            .userId(userId)
                            .build());
                }
            } catch (Exception e) {
                log.error("Error parsing value for key {}: {}", key, e.getMessage());
            }
        }

        return results;
    }

    public void removeSeatHold(SeatHoldRequest seatHoldRequest) {
        String scheduleId = seatHoldRequest.getScheduleId();
        String seatId = seatHoldRequest.getSeatId();
        String userId = seatHoldRequest.getUserId();

        // Tạo lại key đúng định dạng
        String key = String.format("seat_hold:%s:%s:%s", scheduleId, userId, seatId);

        // Xóa key khỏi Redis
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Deleted seat hold for key: {}", key);
        } else {
            log.warn("No seat hold found for key (or already expired): {}", key);
        }
    }

    public void removeSeatHoldsByScheduleIdAndUserId(String scheduleId, String userId) {
        // Pattern để tìm tất cả ghế thuộc scheduleId và userId
        String pattern = String.format("seat_hold:%s:%s:*", scheduleId, userId);
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("No keys found for scheduleId {} and userId {}", scheduleId, userId);
            return;
        }

        Long deletedCount = redisTemplate.delete(keys);
        log.info("Deleted {} keys for scheduleId {} and userId {}", deletedCount, scheduleId, userId);
    }

    public String saveTicketHold(TicketRequest request) throws JsonProcessingException {
        String scheduleId = request.getScheduleId();
        String userId = request.getUserId();
        List<String> seatIds = request.getSeatIds();

        String uuid = UUID.randomUUID().toString();
        String key = String.format("ticket_hold:%s", uuid);

        // Tạo object và chuyển sang JSON
        Map<String, Object> value = new HashMap<>();
        value.put("schedule_id", scheduleId);
        value.put("user_id", userId);
        value.put("seat_ids", seatIds);

        String jsonValue = objectMapper.writeValueAsString(value);

        // Lưu vào Redis với TTL 5 phút (300 giây)
        redisTemplate.opsForValue().set(key, jsonValue, Duration.ofMinutes(5));

        return uuid;
    }

    public void resetTTLByScheduleIdAndUserId(String scheduleId, String userId, Duration newTTL) {
        String pattern = String.format("seat_hold:%s:%s:*", scheduleId, userId);
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("No keys found for scheduleId {} and userId {}", scheduleId, userId);
            return;
        }

        for (String key : keys) {
            Boolean exists = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(exists)) {
                redisTemplate.expire(key, newTTL);
                log.info("Reset TTL for key {} to {} seconds", key, newTTL.getSeconds());
            }
        }
    }


}


