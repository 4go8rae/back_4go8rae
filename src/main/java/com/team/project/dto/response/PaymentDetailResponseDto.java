package com.team.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentDetailResponseDto {
    private Long merchant_uid;
    private String name;
    private int amount;
    private String buyer_addr;
    private String buyer_email;
    private String buyer_name;
    private String buyer_tel;
    private String emb_pg_provider;
    private String pay_method;
    private String card_name;
    private String card_number;
    private String paid_at;

}
