package com.scrable.bitirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemDto {
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
}
