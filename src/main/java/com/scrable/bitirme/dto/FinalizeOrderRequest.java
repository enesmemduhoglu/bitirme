package com.scrable.bitirme.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalizeOrderRequest {
    private Long userId;
    private String paymentIntentId;
}
