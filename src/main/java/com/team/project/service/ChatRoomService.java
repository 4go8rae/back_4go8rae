package com.team.project.service;

import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.domain.Product;
import com.team.project.dto.request.ChatRoomDto;
import com.team.project.dto.response.MemberResponseDto;
import com.team.project.dto.response.ProductResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.ChatRoomRepository;
import com.team.project.repository.MemberRepository;
import com.team.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ChatRoomDto.Response joinChatRoom(ChatRoomDto.Request dto, UserDetailsImpl userDetails) throws IllegalStateException {

        Member customer = memberRepository.findById(userDetails.getMember().getId()).orElse(null);
        if (customer == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Member seller = memberRepository.findById(dto.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Product product = productRepository.findByMemberId(dto.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        if(dto.getCustomerId().equals(dto.getSellerId())) {
            throw new IllegalStateException("자신과의 채팅방은 만들 수 없습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findByCustomer_IdAndSeller_IdAndProduct_Id(dto.getCustomerId(), dto.getSellerId(), dto.getProductId()).orElse(null);
        if (chatRoom != null) {
            return ChatRoomDto.Response.of(chatRoom);

        } else {
            ChatRoom newChatRoom = new ChatRoom(dto);
            chatRoomRepository.save(newChatRoom);
            return ChatRoomDto.Response.builder()
                    .roomId(newChatRoom.getId())
                    .seller(MemberResponseDto.of(seller))
                    .customer(MemberResponseDto.of(customer))
                    .product(ProductResponseDto.builder()
                            .product_id(product.getId())
                            .title(product.getTitle())
                            .price(product.getPrice())
                            .seller_id(product.getMember().getId())
                            .build())
                    .build();
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
