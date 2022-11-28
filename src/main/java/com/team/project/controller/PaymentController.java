package com.team.project.controller;


import com.team.project.dto.request.PaymentConfirmRequestDto;
import com.team.project.dto.request.PaymentRequestDto;
import com.team.project.dto.response.PaymentResponseDto;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/auth/payment")
    public ResponseEntity<PaymentResponseDto> payment(@RequestBody List<PaymentRequestDto> requestDtos, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.payment(requestDtos, userDetails);
    }

    @PostMapping("/api/auth/payment/confirm")
    public ResponseEntity<Boolean> payment(@RequestBody PaymentConfirmRequestDto requestDtos) {
        return paymentService.paymentConfirm(requestDtos);
    }

    @GetMapping("/api/auth/payment")
    public ResponseEntity<List<PaymentResponseDto>> payment(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.getPaymentList(userDetails);
    }
}
