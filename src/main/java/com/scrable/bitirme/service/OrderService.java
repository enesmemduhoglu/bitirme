package com.scrable.bitirme.service;

import com.scrable.bitirme.exception.InsufficientStockException;
import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.*;
import com.scrable.bitirme.repository.CartRepo;
import com.scrable.bitirme.repository.OrderRepo;
import com.scrable.bitirme.repository.ProductRepo;
import com.scrable.bitirme.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;

    @Transactional
    public Order createOrderFromCart(Long userId, String paymentIntentId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Cart> cartItems = cartRepo.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");
        order.setStripePaymentIntentId(paymentIntentId);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();

            if (product.getProductStock() == null || product.getProductStock() < requestedQuantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName() + ". Requested: " + requestedQuantity + ", Available: " + product.getProductStock());
            }

            product.setProductStock(product.getProductStock() - requestedQuantity);
            productRepo.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItem.setPriceAtPurchase(product.getProductPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        BigDecimal totalAmount = orderItems.stream()
                .map(orderItem -> orderItem.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepo.save(order);

        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }
}
