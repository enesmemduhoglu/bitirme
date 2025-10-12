package com.scrable.bitirme.controller;

import com.scrable.bitirme.dto.ConfirmPaymentRequest;
import com.scrable.bitirme.dto.FinalizeOrderRequest;
import com.scrable.bitirme.dto.PaymentRequest;
import com.scrable.bitirme.dto.PaymentResponse;
import com.scrable.bitirme.service.OrderService;
import com.scrable.bitirme.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/payments/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentRequest.getUserId());
            return ResponseEntity.ok(new PaymentResponse(paymentIntent.getId(), paymentIntent.getClientSecret()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error creating payment intent: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/orders/finalize")
    public ResponseEntity<?> finalizeOrder(@RequestBody FinalizeOrderRequest finalizeOrderRequest) {
        try {
            orderService.createOrderFromCart(finalizeOrderRequest.getUserId(), finalizeOrderRequest.getPaymentIntentId());
            return ResponseEntity.ok(Collections.singletonMap("message", "Order created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/payments/confirm-for-testing")
    public ResponseEntity<?> confirmPaymentForTesting(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        try {
            PaymentIntent paymentIntent = paymentService.confirmPaymentIntentForTesting(confirmPaymentRequest.getPaymentIntentId());
            String status = paymentIntent.getStatus();
            return ResponseEntity.ok(Collections.singletonMap("status", status));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error confirming payment intent: " + e.getMessage()));
        }
    }

}
