package com.team.project.service;


import com.team.project.domain.CartItem;
import com.team.project.domain.Member;
import com.team.project.domain.Payment;
import com.team.project.domain.Product;
import com.team.project.dto.request.PaymentConfirmRequestDto;
import com.team.project.dto.request.PaymentRequestDto;
import com.team.project.dto.response.PaymentResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.CartItemRepository;
import com.team.project.repository.MemberRepository;
import com.team.project.repository.PaymentRepository;
import com.team.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public ResponseEntity<PaymentResponseDto> payment(List<PaymentRequestDto> requestDtos, UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        int total = 0;
        String title = "";
        Payment payment;
        Long id = System.currentTimeMillis();

        PaymentRequestDto requestDto = requestDtos.get(0);
        Product product = (productRepository.findById(requestDto.getProduct_id()).orElse(null));

        if (product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        title = product.getTitle();
        total += requestDto.getCount() * product.getPrice();

        if (requestDto.getCart_id() != null) {

            for (int i = 1; i < requestDtos.size(); i++) {
                PaymentRequestDto paymentRequestDto = requestDtos.get(i);
                product = (productRepository.findById(paymentRequestDto.getProduct_id()).orElse(null));
                if (product == null)
                    throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
                total += paymentRequestDto.getCount() * product.getPrice();

                CartItem cartItem = cartItemRepository.findById(paymentRequestDto.getCart_id()).orElse(null);
                if (cartItem == null)
                    throw new CustomException(ErrorCode.NOT_FOUND_ITEM);

                cartItem.setPayment_id(id);
            }
            ;

            if (requestDtos.size() > 1)
                title += " 외 " + (requestDtos.size() - 1);

        }

        payment = Payment.builder()
                .id(id)
                .title(title)
                .price(total)
                .member(member)
                .build();

        paymentRepository.save(payment);


        return ResponseEntity.ok(PaymentResponseDto.builder()
                .merchant_uid(payment.getId())
                .title(payment.getTitle())
                .price(payment.getPrice())
                .build());
    }


    public ResponseEntity<Boolean> paymentConfirm(PaymentConfirmRequestDto requestDtos) {
        Payment payment = paymentRepository.findById(requestDtos.getMerchant_uid()).orElse(null);

        if(payment == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        return ResponseEntity.ok(payment.getPrice()==requestDtos.getPrice());
    }


    public ResponseEntity<List<PaymentResponseDto>> getPaymentList(UserDetailsImpl userDetails){

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<Payment> paymentList = paymentRepository.findAllByMember(member);
        List<PaymentResponseDto> responseDtos = new ArrayList<>();

        for (Payment payment:paymentList) {
            responseDtos.add(PaymentResponseDto.builder()
                    .merchant_uid(payment.getId())
                    .title(payment.getTitle())
                    .price(payment.getPrice())
                    .build());
        }

        return ResponseEntity.ok(responseDtos);
    }



//    public String getToken(){
//        String access;
//
//        return access;
//    }



}





