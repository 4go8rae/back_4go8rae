package com.team.project.controller;


import com.team.project.dto.request.CartItemRequestDto;
import com.team.project.dto.request.DeleteCartItemRequestDto;
import com.team.project.dto.response.CartItemResponseDto;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    
    //카트에 추가
    @PostMapping("/api/auth/cart")
    public ResponseEntity<CartItemResponseDto> createCartItem(@RequestBody CartItemRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cartItemService.createCartItem(requestDto, userDetails);
    }

    //카트 조회
    @GetMapping("/api/auth/cart")
    public ResponseEntity<List<CartItemResponseDto>> getCartItem(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cartItemService.getCartItem(userDetails);
    }

    //카트 상품삭제
    @PostMapping("/api/auth/cart/delete")
    public ResponseEntity<String> deleteCartItem(@RequestBody DeleteCartItemRequestDto requestDto) {
        return cartItemService.deleteCartItem(requestDto);
    }

    //카트 상품 개수 변경
    @PutMapping("/api/auth/cart")
    public ResponseEntity<CartItemResponseDto> updateCartItem(@RequestBody CartItemRequestDto requestDto) {
        return cartItemService.updateCartItem(requestDto);
    }
}
