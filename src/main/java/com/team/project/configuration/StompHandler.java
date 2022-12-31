package com.team.project.configuration;

import com.team.project.jwt.JwtTokenProvider;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        //stomp헤더에서 토큰 가져오기 : 클라이언트단에서 웹 소켓 요청시 헤더값에 토큰검증값을 얻을수 있다.
        //StompHeaderAccessor 를 통해서 세션의 데이터를 얻어올 수 있다(각종 웹소켓 관련 정보)
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        //StompCommand.CONNECT : command로 connect를 시도할 때 로그인 여부 확인
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            String jwtToken = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization")).orElse("UnknownUser");
            log.info("CONNECT 시 토큰이 들어오는지 : " + jwtToken);

            //Header의 jwt token 검증
            boolean isOkToken = jwtTokenProvider.validateToken(jwtToken);
            log.info("CONNECT 시 토큰검증 : " + isOkToken);

            //StompCommand.SUBSCRIBE : 구독 요청이 들어왔을 때 user가 일치하는지 확인하고 일치한다면 채팅방 목록에 추가함.
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            String roomId = Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId");
            log.info("SUBSCRIBE 시 roomId 들어오는지 : " + roomId);
        }
        return message;
    }

    /*
    spring boot로 websocket을 개발하다보면 Session의 Connect / Disconnect 되는 시점을 알고 싶을 때가 있는데
    (사용자 동시 접속 리스트 관리 차원)
    */
    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        switch (accessor.getCommand()) {
            case CONNECT:
                // 유저가 Websocket으로 connect()를 한 뒤 호출됨

                break;
            case DISCONNECT:
                // 유저가 Websocket으로 disconnect() 를 한 뒤 호출됨 or 세션이 끊어졌을 때 발생함(페이지 이동~ 브라우저 닫기 등)
                break;
            default:
                break;
        }
    }
}
