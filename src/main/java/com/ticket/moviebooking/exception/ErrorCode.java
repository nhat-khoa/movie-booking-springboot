package com.ticket.moviebooking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    MOVIE_NOT_EXISTED(1007, "Movie not existed", HttpStatus.NOT_FOUND),
    ROOM_NOT_EXISTED(1008, "Room not existed", HttpStatus.NOT_FOUND),
    USER_NOT_EXISTED(1009, "User not existed", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_EXISTED(1010, "Schedule not existed", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
