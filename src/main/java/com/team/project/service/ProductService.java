package com.team.project.service;

import com.team.project.domain.Product;
import com.team.project.dto.request.ProductRequestDto;
import com.team.project.dto.response.ProductResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.repository.CartItemRepository;
import com.team.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    //상품 추가
    @Transactional
    public ResponseEntity<ProductResponseDto> createProduct(ProductRequestDto requestDto) {

        Product product = Product.builder()
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .build();

        productRepository.save(product);


        return ResponseEntity.ok(ProductResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .image(product.getImage())
                .build());
    }

    //상품 조회
    public ResponseEntity<ProductResponseDto> getProduct(Long id) {

        Product product = productRepository.findById(id).orElse(null);
        if(product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        return ResponseEntity.ok(ProductResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .image(product.getImage())
                .build());
    }

    //상품 조회
    public ResponseEntity<List<ProductResponseDto>> getProductList(int page) {

        List<Product> products = productRepository.findAll(PageRequest.of(page, 30)).toList();

        List<ProductResponseDto> productList = new ArrayList<>();
        for (Product product:products) {
            productList.add(ProductResponseDto.builder()
                    .id(product.getId())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .image(product.getImage())
                    .build());

        }
        return ResponseEntity.ok(productList);
    }

    //상품 삭제
    @Transactional
    public ResponseEntity<String> deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElse(null);
        if(product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        cartItemRepository.deleteAllByProduct(product);
        productRepository.delete(product);

        return ResponseEntity.ok("delete product: "+id);
    }


}





