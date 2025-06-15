package com.ticket.moviebooking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.dto.request.PaymentCreateRequest;
import com.ticket.moviebooking.dto.request.SeatHoldRequest;
import com.ticket.moviebooking.dto.response.ScheduleResponse;
import com.ticket.moviebooking.dto.response.SeatHoldResponse;
import com.ticket.moviebooking.dto.response.SeatResponse;
import com.ticket.moviebooking.dto.response.TicketHoldResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisService {

    RedisTemplate<String, String> redisTemplate;
    ObjectMapper objectMapper;
    ScheduleService scheduleService;
    SeatService seatService;

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
        redisTemplate.opsForValue().set(key, jsonValue, Duration.ofMinutes(3));
    }

    public Boolean setSeatHoldTTL(SeatHoldRequest seatHoldRequest) {
        String scheduleId = seatHoldRequest.getScheduleId();
        String seatId = seatHoldRequest.getSeatId();
        String userId = seatHoldRequest.getUserId();

        // Tạo key theo định dạng
        String key = String.format("seat_hold:%s:%s:%s", scheduleId, userId, seatId);

        // Set TTL lại (ví dụ: 15 phút)
        return redisTemplate.expire(key, Duration.ofMinutes(15));
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

    public void saveTicketHold(String appTransId, String orderUrl, PaymentCreateRequest request) throws JsonProcessingException {
        String key = String.format("ticket_hold:%s:%s", request.getUserId(), appTransId);

        // Tạo object và chuyển sang JSON
        Map<String, Object> value = new HashMap<>();
        value.put("app_trans_id", appTransId);
        value.put("schedule_id", request.getScheduleId());
        value.put("user_id", request.getUserId());
        value.put("seat_ids", request.getSeatIds());
        value.put("total_price", request.getTotalPrice());
        value.put("order_url", orderUrl);

        String jsonValue = objectMapper.writeValueAsString(value);
        // Lưu vào Redis với TTL 5 phút (300 giây)
        redisTemplate.opsForValue().set(key, jsonValue, Duration.ofMinutes(15));
    }

    public void removeTicketHoldsByAppTransIdAndUserId(String appTransId, String userId) {

        String pattern = String.format("ticket_hold:%s:%s",userId, appTransId);
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            log.info("No keys found for appTransId: {}", appTransId);
            return;
        }

        Long deletedCount = redisTemplate.delete(keys);
        log.info("Deleted {} keys for appTransId: {}", deletedCount, appTransId);
    }

    public List<TicketHoldResponse> findAllTicketHoldByUserId() throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        List<TicketHoldResponse> result = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("ticket_hold:" + userId + ":*");

        if (keys == null || keys.isEmpty()) return result;

        for (String key : keys) {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) continue;

            // Parse value từ Redis
            Map<String, Object> value = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

            // Map sang DTO
            TicketHoldResponse response = new TicketHoldResponse();
            response.setAppTransId((String) value.get("app_trans_id"));
            response.setTotalPrice((Integer) value.get("total_price"));
            response.setOrderUrl((String) value.get("order_url"));

            // Lấy TTL (seconds)
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            response.setTimeToLive(ttl);

            // Convert schedule_id và seat_ids
            String scheduleId = (String) value.get("schedule_id");
            List<String> seatIds = (List<String>) value.get("seat_ids");

            // Gọi các service để lấy dữ liệu chi tiết
            ScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);
            List<SeatResponse> seats = seatService.findByIds(seatIds);

            response.setSchedule(schedule);
            response.setSeats(seats);

            result.add(response);
        }

        return result;
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


