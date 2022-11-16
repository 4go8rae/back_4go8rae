package com.team.project.service;

import com.team.project.domain.CartItem;
import com.team.project.domain.Member;
import com.team.project.domain.Product;
import com.team.project.dto.request.CartItemRequestDto;
import com.team.project.dto.request.DeleteCartItemRequestDto;
import com.team.project.dto.response.CartItemResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.CartItemRepository;
import com.team.project.repository.MemberRepository;
import com.team.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    //장바구니 상품 추가
    @Transactional
    public ResponseEntity<CartItemResponseDto> createCartItem(CartItemRequestDto requestDto, UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);
        Product product = productRepository.findById(requestDto.getId()).orElse(null);
        if (product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        CartItem cartItem = cartItemRepository.findByMemberAndProduct(member, product).orElse(null);

        if(cartItem != null) {
            cartItem.update(cartItem.getCount()+requestDto.getCount());
        }

        else {
            cartItem = CartItem.builder()
                    .count(requestDto.getCount())
                    .product(product)
                    .total(product.getPrice() * requestDto.getCount())
                    .member(member)
                    .build();

            cartItemRepository.save(cartItem);
        }

        return ResponseEntity.ok(CartItemResponseDto.builder()
                .id(cartItem.getId())
                .product_id(cartItem.getProduct().getId())
                .title(cartItem.getProduct().getTitle())
                .count(cartItem.getCount())
                .total(cartItem.getTotal())
                .build());
    }

    //장바구니 조회
    public ResponseEntity<List<CartItemResponseDto>> getCartItem(UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<CartItem> cartItemList = cartItemRepository.findAllByMember(member);
        List<CartItemResponseDto> itemList = new ArrayList<>();
        for (CartItem item : cartItemList) {
            itemList.add(CartItemResponseDto.builder()
                    .id(item.getId())
                    .product_id(item.getProduct().getId())
                    .title(item.getProduct().getTitle())
                    .count(item.getCount())
                    .total(item.getTotal())
                    .build());
        }
        return ResponseEntity.ok(itemList);
    }


    //장바구니 상품 삭제
    @Transactional
    public ResponseEntity<String> deleteCartItem(DeleteCartItemRequestDto requestDto) {
        StringBuilder result = new StringBuilder("delete cartItem: ");
        for (Long id: requestDto.getIdList()) {
            CartItem cartItem = cartItemRepository.findById(id).orElse(null);
            if (cartItem == null)
                throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

            cartItemRepository.delete(cartItem);
            result.append(id+" ");
        }
        return ResponseEntity.ok(result.toString());
    }


    //상품 삭제
    @Transactional
    public ResponseEntity<CartItemResponseDto> updateCartItem(CartItemRequestDto cartItemRequestDto) {

        CartItem cartItem = cartItemRepository.findById(cartItemRequestDto.getId()).orElse(null);
        if (cartItem == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        cartItem.update(cartItemRequestDto.getCount());

        return ResponseEntity.ok(CartItemResponseDto.builder()
                .id(cartItem.getId())
                .product_id(cartItem.getProduct().getId())
                .title(cartItem.getProduct().getTitle())
                .count(cartItem.getCount())
                .total(cartItem.getTotal())
                .build());
    }

    
}





