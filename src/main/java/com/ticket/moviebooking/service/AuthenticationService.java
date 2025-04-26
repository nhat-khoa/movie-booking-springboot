package com.ticket.moviebooking.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ticket.moviebooking.dto.response.AuthenticationResponse;
import com.ticket.moviebooking.dto.request.AuthenticationRequest;
import com.ticket.moviebooking.entity.User;
import com.ticket.moviebooking.mapper.UserMapper;
import com.ticket.moviebooking.repository.UserRepository;
import com.ticket.moviebooking.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    UserMapper userMapper;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    public AuthenticationResponse loginWithGoogle(AuthenticationRequest request) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(request.getCredential());
        if (idToken == null) throw new IllegalArgumentException("Invalid Google token");

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String fullName = (String) payload.get("name");
        String avatarUrl = ((String) payload.get("picture")).replace("s96-c", "s400");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(fullName);
                    newUser.setAvatarGoogleUrl(avatarUrl);
                    return userRepository.save(newUser);
                });

        String accessToken = JwtUtil.generateAccessToken(user);
        String refreshToken = JwtUtil.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toUserResponse(user))
                .authenticated(true)
                .build();
    }
}
