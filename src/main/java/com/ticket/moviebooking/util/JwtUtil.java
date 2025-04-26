package com.ticket.moviebooking.util;

import com.ticket.moviebooking.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Key ngẫu nhiên, nên lưu cố định
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 60 phút
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 ngày

    public static String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("fullName", user.getFullName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public static String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String getEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }
}
