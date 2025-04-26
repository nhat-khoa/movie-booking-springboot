package com.ticket.moviebooking.dto.response;

import com.ticket.moviebooking.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String email;
    String avatarGoogleUrl;
    String fullName;
    String phoneNumber;
    LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    Gender gender;
}
