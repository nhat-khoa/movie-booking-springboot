package com.ticket.moviebooking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgeRating {
    AGE_12_PLUS("12+"),
    AGE_13_PLUS("13+"),
    AGE_18_PLUS("18+"),
    AGE_21_PLUS("21+"),
    AGE_UNRATED("Unrated");

    private final String value;
}