package com.ticket.moviebooking.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket (client sẽ kết nối tới đây)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép từ mọi domain (hoặc cấu hình cụ thể)
                .withSockJS(); // Sử dụng SockJS để hỗ trợ fallback cho các trình duyệt không hỗ trợ WebSocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix cho các topic client sẽ subscribe
        config.enableSimpleBroker("/topic");

        // Prefix cho client gửi message lên server
        config.setApplicationDestinationPrefixes("/app");
    }
}

