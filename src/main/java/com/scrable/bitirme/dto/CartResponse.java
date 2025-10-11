package com.scrable.bitirme.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartResponse {
    private long userId;
    private long productId;
    private Integer quantity;

    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private String productDescription;
}
