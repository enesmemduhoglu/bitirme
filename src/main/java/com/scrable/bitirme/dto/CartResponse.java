package com.scrable.bitirme.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponse {
    private long userId;
    private long productId;
    private Integer quantity;

    private String productName;
    private String productImage;
    private Double productPrice;
    private String productDescription;
}
