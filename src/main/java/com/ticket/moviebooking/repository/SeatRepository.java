package com.ticket.moviebooking.repository;

import com.ticket.moviebooking.entity.Seat;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findByRoomId(String roomId, Sort sort);
}
