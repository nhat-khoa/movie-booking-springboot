package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.SeatResponse;
import com.ticket.moviebooking.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(source="room.id", target="roomId")
    SeatResponse toSeatResponse(Seat seat);
}
