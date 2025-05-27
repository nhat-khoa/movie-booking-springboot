package com.ticket.moviebooking.controller;

import com.nimbusds.jose.JOSEException;
import com.ticket.moviebooking.dto.ApiResponse;
import com.ticket.moviebooking.dto.request.AuthenticationRequest;
import com.ticket.moviebooking.dto.response.AuthenticationResponse;
import com.ticket.moviebooking.dto.response.IntrospectResponse;
import com.ticket.moviebooking.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

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

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replaceFirst("^Bearer\\s+", "");
        var result = authenticationService.introspect(token);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }
}
