package com.scrable.bitirme.service;

import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.Cart;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.repository.CartRepo;
import com.scrable.bitirme.repository.UserRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public PaymentIntent createPaymentIntent(Long userId) throws StripeException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Cart> cartItems = cartRepo.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create payment for an empty cart.");
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getProductPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalAmount.multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .build();

        return PaymentIntent.create(params);
    }

    public PaymentIntent confirmPaymentIntentForTesting(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        paymentIntent = paymentIntent.confirm(
                com.stripe.param.PaymentIntentConfirmParams.builder()
                        .setPaymentMethod("pm_card_visa")
                        .build());

        return paymentIntent;
    }
}
