package com.scrable.bitirme.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPaymentRequest {
    private String paymentIntentId;
}
