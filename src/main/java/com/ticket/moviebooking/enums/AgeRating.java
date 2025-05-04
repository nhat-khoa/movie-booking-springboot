package com.ticket.moviebooking.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgeRating {
    AGE_12_PLUS("12+"),
    AGE_13_PLUS("13+"),
    AGE_16_PLUS("16+"),
    AGE_18_PLUS("18+"),
    AGE_21_PLUS("21+"),
    AGE_UNRATED("0+"),
    AGE_UNKNOWN("?");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}