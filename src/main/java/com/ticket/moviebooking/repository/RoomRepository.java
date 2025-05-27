package com.ticket.moviebooking.repository;

import com.ticket.moviebooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {
}
