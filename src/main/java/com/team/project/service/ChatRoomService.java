package com.team.project.service;

import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.dto.request.ChatRoomDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.ChatRoomRepository;
import com.team.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // Redis CacheKeys
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatRoomDto.Create joinChatRoom(ChatRoomDto.Request dto, UserDetailsImpl userDetails) throws IllegalStateException {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        if(dto.getCustomerId().equals(dto.getSellerId())) {
            throw new IllegalStateException("자신과의 채팅방은 만들 수 없습니다.");
        }

        Optional<ChatRoom> chatRoom = chatRoomRepository.findByCustomer_IdAndSeller_IdAndProduct_Id(dto.getCustomerId(), dto.getSellerId(), dto.getProductId());
        if (chatRoom.isPresent()) {
            return ChatRoomDto.Create.builder()
                    .roomId(chatRoom.get().getId())
                    .sellerId(chatRoom.get().getSeller().getId())
                    .customerId(chatRoom.get().getCustomer().getId())
                    .productId(chatRoom.get().getProduct().getId())
                    .build();

        } else {
            ChatRoom newChatRoom = new ChatRoom(dto);
            chatRoomRepository.save(newChatRoom);
            return ChatRoomDto.Create.of(newChatRoom);
        }
    }

    @Transactional
    public List<ChatRoomDto.Response> getRoomList(Long memberId) {
        return chatRoomRepository.findAllByMemberId(memberId).stream().map(ChatRoomDto.Response::of).collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomDto.Detail getRoomDetail(Long roomId) {
        Optional<ChatRoomDto.Detail> room = chatRoomRepository.findById(roomId).map(ChatRoomDto.Detail::of);
        return room.orElseThrow();
    }

}
