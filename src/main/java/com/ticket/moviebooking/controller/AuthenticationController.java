package com.ticket.moviebooking.controller;

import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.AuthenticationRequest;
import com.ticket.moviebooking.dto.response.AuthenticationResponse;
import com.ticket.moviebooking.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login-google")
    ApiResponse<AuthenticationResponse> loginWithGoogle(@RequestBody AuthenticationRequest request) throws GeneralSecurityException, IOException {
        var result = authenticationService.loginWithGoogle(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }
}
