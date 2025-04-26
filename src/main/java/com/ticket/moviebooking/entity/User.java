package com.ticket.moviebooking.entity;

import com.ticket.moviebooking.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "email", unique = true)
    String email;

    @Column(length = 500)
    String avatarGoogleUrl;

    String fullName;
    String phoneNumber;
    LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    Gender gender;

}
