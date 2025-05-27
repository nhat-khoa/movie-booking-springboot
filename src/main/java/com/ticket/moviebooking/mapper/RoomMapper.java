package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.RoomResponse;
import com.ticket.moviebooking.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomResponse toMovieResponse(Room room);
}
