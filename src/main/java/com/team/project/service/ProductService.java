package com.team.project.service;

import com.team.project.domain.Member;
import com.team.project.domain.Product;
import com.team.project.dto.request.ProductRequestDto;
import com.team.project.dto.response.ProductResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.CartItemRepository;
import com.team.project.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    //상품 추가
    @Transactional
    public ResponseEntity<ProductResponseDto> createProduct(ProductRequestDto requestDto, UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        Product product = Product.builder()
                .title(requestDto.getTitle())
                .price(requestDto.getPrice())
                .member(member)
                .build();

        productRepository.save(product);


        return ResponseEntity.ok(ProductResponseDto.builder()
                .product_id(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .image(product.getImage())
                .seller_id(product.getMember().getId())
                .build());
    }

    //상품 조회
    public ResponseEntity<ProductResponseDto> getProduct(Long id) {

        Product product = productRepository.findById(id).orElse(null);
        if(product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        return ResponseEntity.ok(ProductResponseDto.builder()
                .product_id(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .image(product.getImage())
                .seller_id(product.getMember().getId())
                .build());
    }

    //상품 조회
    public ResponseEntity<List<ProductResponseDto>> getProductList(int page) {

        List<Product> products = productRepository.findAll(PageRequest.of(page, 30)).toList();

        List<ProductResponseDto> productList = new ArrayList<>();
        for (Product product:products) {
            productList.add(ProductResponseDto.builder()
                    .product_id(product.getId())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .image(product.getImage())
                    .seller_id(product.getMember().getId())
                    .build());

        }
        return ResponseEntity.ok(productList);
    }

    //상품 삭제
    @Transactional
    public ResponseEntity<String> deleteProduct(Long id, UserDetailsImpl userDetails) {

        Product product = productRepository.findById(id).orElse(null);
        if(product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        isSameSeller(userDetails, product.getMember());

        cartItemRepository.deleteAllByProduct(product);
        productRepository.delete(product);

        return ResponseEntity.ok("delete product: "+id);
    }


    public Member isSameSeller(UserDetailsImpl userDetails, Member seller) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        if(!member.getUsername().equals(seller.getUsername()))
            throw new CustomException(ErrorCode.DO_NOT_MATCH_USER);
        return member;
    }
}





