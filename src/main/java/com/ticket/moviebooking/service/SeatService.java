package com.ticket.moviebooking.service;

import com.ticket.moviebooking.dto.response.SeatResponse;
import com.ticket.moviebooking.entity.Seat;
import com.ticket.moviebooking.mapper.SeatMapper;
import com.ticket.moviebooking.repository.SeatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatService {
    SeatRepository seatRepository;
    SeatMapper seatMapper;

    public List<SeatResponse> findByRoomId(String roomId){
        Sort sort = Sort.by("line").ascending().and(Sort.by("number").ascending());
        return seatRepository.findByRoomId(roomId, sort)
                .stream()
                .map(seatMapper::toSeatResponse)
                .toList();
    }

    public SeatResponse findById(String seatId) {
        return seatRepository.findById(seatId)
                .map(seatMapper::toSeatResponse)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + seatId));
    }
    public List<SeatResponse> findByIds(List<String> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats not found.");
        }

        return seats.stream()
                .map(seatMapper::toSeatResponse)
                .collect(Collectors.toList());
    }

}
