package com.team.project.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
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
     * enableSimpleBroker : 메시지를 받을 때의 경로 설정, "/sub" 이 api에 prefix로 붙은 경우, message broker 가 인터셉트 한다.
     * setApplicationDestinationPrefixes : 메시지를 보낼 때의 경로 설정, 클라이언트가 메시지를 보낼 때 api에 "/pub"이 prefix로 붙은 경우
     * broker로 메시지가 보내진다.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

}