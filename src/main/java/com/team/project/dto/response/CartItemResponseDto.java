package com.team.project.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartItemResponseDto {
    private Long id;
    private Long cartItem_id;
    private String title;
    private int total;
    private int count;
    private String image;
}
