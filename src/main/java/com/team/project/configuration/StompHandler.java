package com.team.project.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
//    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        /**
         * stomp헤더에서 토큰 가져오기 : 클라이언트단에서 웹 소켓 요청시 헤더값에 토큰검증값을 얻을수 있다.
         * StompHeaderAccessor 를 통해서 세션의 데이터를 얻어올 수 있다(각종 웹소켓 관련 정보)
         */
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);


        /**
         * StompCommand.CONNECT : command로 connect를 시도할 때 로그인 여부 확인
         * StompCommand.SUBSCRIBE : 구독 요청이 들어왔을 때 user가 일치하는지 확인하고 일치한다면 채팅방 목록에 추가
         */
        if (StompCommand.CONNECT == accessor.getCommand()) {
//            String jwtToken = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization")).orElse("Unknown User");
//            log.info("jwtToken = {}", jwtToken);
//
//            jwtTokenProvider.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String roomId = getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            log.info("SUBSCRIBE roomId = {}", roomId);
        }
        return message;
    }

    /**
     * 사용자 동시 접속 리스트 관리
     * CONNECT : 사용자가 Websocket으로 connect() 한 뒤 호출
     * DISCONNECT : 사용자가 disconnect() 하거나 세션이 끊어졌을 경우 호출(페이지 이동, 브라우저 닫기 등)
     */
    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        switch (accessor.getCommand()) {
            case CONNECT:
                log.info("Connect User : {}", "유저 연결됨");

                break;
            case DISCONNECT:
                log.info("Disconnect User : {}", "유저 연결해제됨");
                break;
            default:
                break;
        }
    }

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            throw new IllegalArgumentException("lastIndex 오류입니다.");
    }
}
