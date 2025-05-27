package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.SeatResponse;
import com.ticket.moviebooking.dto.response.TicketResponse;
import com.ticket.moviebooking.entity.Ticket;
import com.ticket.moviebooking.entity.TicketSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ScheduleMapper.class, UserMapper.class})
public abstract class TicketMapper {

    @Autowired
    protected SeatMapper seatMapper;

    @Mapping(source = "user", target = "user")
    @Mapping(source = "schedule", target = "schedule")
    @Mapping(source = "ticketSeats", target = "seatList")
    public abstract TicketResponse toTicketResponse(Ticket ticket);

    protected List<SeatResponse> mapSeats(List<TicketSeat> ticketSeats) {
        if (ticketSeats == null) return List.of();
        return ticketSeats.stream()
                .sorted(Comparator
                        .comparing((TicketSeat ts) -> ts.getSeat().getLine())
                        .thenComparing(ts -> ts.getSeat().getNumber()))
                .map(ticketSeat -> seatMapper.toSeatResponse(ticketSeat.getSeat()))
                .toList();
    }
}
