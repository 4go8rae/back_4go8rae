package com.team.project.controller;



import com.team.project.dto.request.ItemRequestDto;
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
    public ResponseEntity<PaymentResponseDto> payment(@RequestBody List<ItemRequestDto> requestDtos, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return paymentService.payment(requestDtos, userDetails);
    }

}
