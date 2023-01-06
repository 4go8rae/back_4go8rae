package com.team.project.controller;


import com.team.project.dto.request.ProductRequestDto;
import com.team.project.dto.response.ProductResponseDto;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    
    //상품추가
    @PostMapping("/api/auth/product")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return productService.createProduct(requestDto, userDetails);
    }

    //상품조회
    @GetMapping("/api/product/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    //상품조회
    @GetMapping("/api/product")
    public ResponseEntity<List<ProductResponseDto>> getProduct(@RequestParam(value = "page") int page) {
        return productService.getProductList(page);
    }

    //상품삭제
    @DeleteMapping("/api/auth/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return productService.deleteProduct(id, userDetails);
    }

}
