package com.scrable.bitirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddToCartResponse {
    private String message;
    private int quantityAdded;
    private long productId;
}