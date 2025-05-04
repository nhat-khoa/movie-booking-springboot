package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.TicketResponse;
import com.ticket.moviebooking.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    @Mapping(source="schedule.id", target = "scheduleId")
    @Mapping(source="user.id", target = "userId")
    TicketResponse toTicketResponse(Ticket ticket);
}
