package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.ScheduleResponse;
import com.ticket.moviebooking.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "room.id", target = "roomId")
    ScheduleResponse toScheduleResponse(Schedule schedule);
}
