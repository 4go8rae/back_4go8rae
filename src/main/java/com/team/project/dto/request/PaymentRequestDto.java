package com.team.project.dto.request;

import lombok.Getter;

@Getter
public class PaymentRequestDto {

    private Long product_id;
    private Long cart_id;
    private int count;


}
