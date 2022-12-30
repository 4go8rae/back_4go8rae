package com.team.project.configuration;

import com.team.project.jwt.JwtTokenProvider;
import com.team.project.service.ChatRoomService;
import com.team.project.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = (String) message.getHeaders().get("simpSessionId");

        if (StompCommand.CONNECT == accessor.getCommand()) {
//            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("Authorization"));
            return message;
        }

        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String roomId = chatService.getRoomId((String) Optional.ofNullable(message.getHeaders().get("simpDestination")).orElse("Invalid roomId"));
            chatService.setUserEnterInfo(roomId, sessionId);

        } else if (StompCommand.UNSUBSCRIBE == accessor.getCommand() || StompCommand.DISCONNECT == accessor.getCommand()) {
            String roomId = chatService.getRoomId((String) Optional.ofNullable(message.getHeaders().get("simpDestination")).orElse("Invalid roomId"));
            chatService.removeUserEnterInfo(roomId, sessionId);
        }

        return message;
    }
}
