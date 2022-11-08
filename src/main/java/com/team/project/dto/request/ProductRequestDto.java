package com.team.project.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class ProductRequestDto {

    private String title;
    private int price;


}
