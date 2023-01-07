package com.team.project.dto.request;

import com.team.project.domain.ChatRoom;
import com.team.project.domain.Member;
import com.team.project.domain.Product;
import com.team.project.dto.response.MemberResponseDto;
import com.team.project.dto.response.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Request {
        private Long sellerId;
        private Long customerId;
        private Long productId;

        public ChatRoom toChatRoom() {
            return ChatRoom.builder()
                    .seller(Member.builder().id(sellerId).build())
                    .customer(Member.builder().id(customerId).build())
                    .product(Product.builder().id(productId).build())
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long roomId;
        private MemberResponseDto seller;
        private MemberResponseDto customer;
        private ProductResponseDto product;

        public static Response of(ChatRoom chatRoom) {
            MemberResponseDto seller = null, customer = null;
            ProductResponseDto product = null;
            if(chatRoom.getSeller() != null) seller = MemberResponseDto.of(chatRoom.getSeller());
            if(chatRoom.getCustomer() != null) customer = MemberResponseDto.of(chatRoom.getCustomer());
            if(chatRoom.getProduct() != null)
                product = ProductResponseDto.builder()
                        .product_id(chatRoom.getProduct().getId())
                        .title(chatRoom.getProduct().getTitle())
                        .price(chatRoom.getProduct().getPrice())
                        .seller_id(chatRoom.getProduct().getMember().getId())
                        .build();

            return Response.builder()
                    .roomId(chatRoom.getId())
                    .seller(seller)
                    .customer(customer)
                    .product(product)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private Long roomId;
        private MemberResponseDto seller;
        private MemberResponseDto customer;
        private ProductResponseDto product;
        private List<ChatMessageDto.Response> messages;

        public static Detail of(ChatRoom chatRoom) {
            return Detail.builder()
                    .roomId(chatRoom.getId())
                    .seller(MemberResponseDto.of(chatRoom.getSeller()))
                    .customer(MemberResponseDto.of(chatRoom.getCustomer()))
                    .product(ProductResponseDto.builder()
                            .product_id(chatRoom.getProduct().getId())
                            .title(chatRoom.getProduct().getTitle())
                            .price(chatRoom.getProduct().getPrice())
                            .seller_id(chatRoom.getProduct().getMember().getId())
                            .build())
                    .messages(chatRoom.getMessageList().stream().map(ChatMessageDto.Response::of).collect(Collectors.toList()))
                    .build();
        }
    }

}
