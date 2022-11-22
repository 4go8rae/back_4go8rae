package com.team.project.service;


import com.team.project.domain.Member;
import com.team.project.domain.Payment;
import com.team.project.domain.Product;
import com.team.project.dto.request.ItemRequestDto;
import com.team.project.dto.response.PaymentResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.MemberRepository;
import com.team.project.repository.PaymentRepository;
import com.team.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<PaymentResponseDto> payment(List<ItemRequestDto> requestDtos, UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        int total = 0;
        String title = "";
        for (int i = 0; i< requestDtos.size(); i++ ) {
            ItemRequestDto item = requestDtos.get(i);
            Product product = (productRepository.findById(item.getId()).orElse(null));
            if(product == null)
                throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
            if(i == 0)
                title = product.getTitle();
            total += item.getCount()*product.getPrice();
        }

        if(requestDtos.size() > 1)
            title += " 외 "+(requestDtos.size()-1);

        Payment payment = Payment.builder()
                .id(System.currentTimeMillis())
                .title(title)
                .price(total)
                .member(member)
                .build();

        paymentRepository.save(payment);

        return ResponseEntity.ok(PaymentResponseDto.builder()
                .id(payment.getId())
                .title(payment.getTitle())
                .price(payment.getPrice())
                .build());
    }

    

}





