package com.team.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponseDto {
    private Long cart_id;
    private Long product_id;
    private String title;
    private int total;
    private int count;
    private String image;
}
