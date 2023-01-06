package com.team.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {
    private Long product_id;
    private String title;
    private int price;
    private String image;
    private Long seller_id;
}
