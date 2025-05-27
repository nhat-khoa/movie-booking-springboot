package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.ScheduleResponse;
import com.ticket.moviebooking.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MovieMapper.class, RoomMapper.class})
public interface ScheduleMapper {
    @Mapping(source = "movie", target = "movie")
    @Mapping(source = "room", target = "room")
    ScheduleResponse toScheduleResponse(Schedule schedule);
}
