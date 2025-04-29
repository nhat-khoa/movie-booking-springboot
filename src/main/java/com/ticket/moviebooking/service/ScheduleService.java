package com.ticket.moviebooking.service;

import com.ticket.moviebooking.dto.response.ScheduleResponse;
import com.ticket.moviebooking.mapper.ScheduleMapper;
import com.ticket.moviebooking.repository.ScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleService {
    ScheduleRepository scheduleRepository;
    ScheduleMapper scheduleMapper;

    public List<LocalDate> getSchedules() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        return scheduleRepository.getSchedules(startOfToday)
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct() // loại trùng
                .sorted()   // sắp xếp tăng dần
                .collect(Collectors.toList());
    }

    public Set<String> getMovieByDate(LocalDate localDate) {
        return scheduleRepository.getMovieByDate(localDate);
    }

    public List<ScheduleResponse> findByStartDate(LocalDate targetDate){
        return scheduleRepository.findByStartDate(targetDate)
                .stream()
                .map(scheduleMapper::toScheduleResponse)
                .toList();
    }



}
