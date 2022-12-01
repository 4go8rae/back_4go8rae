package com.team.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponseDto {
    private Long merchant_uid;
    private String title;
    private int price;
}
