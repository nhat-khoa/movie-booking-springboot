package com.ticket.moviebooking.mapper;

import com.ticket.moviebooking.dto.response.UserResponse;
import com.ticket.moviebooking.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
