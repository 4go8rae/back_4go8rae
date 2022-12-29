package com.team.project.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Websocket Handshake를 위한 EndPoint 지정, CORS 설정 및 SockJS 사용 설정
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * STOMP 사용을 위한 Message Broker 설정
     * enableSimpleBroker : 메시지를 받을 때의 경로 설정, "/sub" 으로 시작하는 메시지가 메시지 브로커로 라우팅. 메시지 브로커는 해당 채팅방을 구독하는 클라이언트에게 메시지 전달!
     * setApplicationDestinationPrefixes : "/pub" 으로 시작하는 모든 메시지는 @MessageMapping 이 달린 메서드로 라우팅
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

}