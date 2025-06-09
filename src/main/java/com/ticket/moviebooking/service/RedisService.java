package com.ticket.moviebooking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.dto.request.SeatSelected;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RedisService {

    RedisTemplate<String, String> redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    private String redisKey(String scheduleId) {
        return "schedule:" + scheduleId;
    }

    public List<SeatSelected> findByScheduleId(String scheduleId) {
        String raw = redisTemplate.opsForValue().get(redisKey(scheduleId));
        log.info("raw: {}", raw);
        if (raw == null) return new ArrayList<>();
        try {
            return new ArrayList<>(Arrays.asList(objectMapper.readValue(raw, SeatSelected[].class)));
        } catch (JsonProcessingException e) {
            log.error("Error parsing Redis data", e);
            return new ArrayList<>();
        }
    }

    public void updateSeat(SeatSelected seatSelected) {
        log.info("Adding seat selection for user {} on schedule {}: {}",
                seatSelected.getUserId(),
                seatSelected.getScheduleId(),
                seatSelected.getSeatIds());

        List<SeatSelected> list = findByScheduleId(seatSelected.getScheduleId());

        // Check nếu userId đã có thì cập nhật seatIds, không thì thêm mới
        boolean updated = false;
        for (SeatSelected item : list) {
            if (item.getUserId().equals(seatSelected.getUserId())) {
                item.setSeatIds(seatSelected.getSeatIds());
                updated = true;
                break;
            }
        }

        if (!updated) {
            list.add(seatSelected);
        }

        saveToRedis(seatSelected.getScheduleId(), list);
    }

    public void removeSeats(String scheduleId, String userId, List<String> seatIdsToRemove) {
        List<SeatSelected> list = findByScheduleId(scheduleId);
        List<SeatSelected> updatedList = new ArrayList<>();

        for (SeatSelected item : list) {
            if (item.getUserId().equals(userId)) {
                List<String> remaining = item.getSeatIds().stream()
                        .filter(id -> !seatIdsToRemove.contains(id))
                        .toList();

                if (!remaining.isEmpty()) {
                    item.setSeatIds(remaining);
                    updatedList.add(item);
                }
                // else: không thêm lại nếu không còn ghế nào
            } else {
                updatedList.add(item);
            }
        }

        saveToRedis(scheduleId, updatedList);
    }

    public List<String> findSeatIdsSelectedByOthers(String scheduleId, String userId) {
        return findByScheduleId(scheduleId).stream()
                .filter(s -> !s.getUserId().equals(userId))
                .flatMap(s -> s.getSeatIds().stream())
                .toList();
    }

    private void saveToRedis(String scheduleId, List<SeatSelected> list) {
        try {
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(redisKey(scheduleId), json, 5, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("Error writing to Redis", e);
        }
    }
}


