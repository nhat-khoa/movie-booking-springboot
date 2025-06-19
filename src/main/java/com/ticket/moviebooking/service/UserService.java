package com.ticket.moviebooking.service;

import com.ticket.moviebooking.dto.response.UserResponse;
import com.ticket.moviebooking.entity.User;
import com.ticket.moviebooking.exception.AppException;
import com.ticket.moviebooking.exception.ErrorCode;
import com.ticket.moviebooking.mapper.UserMapper;
import com.ticket.moviebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }
}
