package com.scrable.bitirme.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {
    private long userId;
    private long productId;
    private Integer quantity;
}
