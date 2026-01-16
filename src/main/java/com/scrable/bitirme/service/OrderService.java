package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.OrderDto;
import com.scrable.bitirme.dto.OrderDtoMapper;
import com.scrable.bitirme.exception.InsufficientStockException;
import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.*;
import com.scrable.bitirme.repository.AddressRepo;
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
    private final AddressRepo addressRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final OrderDtoMapper orderDtoMapper;
    private final EmailService emailService;

    @Transactional
    public Order createOrderFromCart(Long userId, String paymentIntentId, Long addressId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Cart> cartItems = cartRepo.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }

        // Validate and fetch Address
        Address shippingAddress = addressRepo.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Security check: ensure address belongs to user
        if (shippingAddress.getUser().getId() != userId) {
            throw new IllegalArgumentException("Address does not belong to the user.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");
        order.setStripePaymentIntentId(paymentIntentId);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();

            if (product.getProductStock() == null || product.getProductStock() < requestedQuantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName()
                        + ". Requested: " + requestedQuantity + ", Available: " + product.getProductStock());
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

        try {
            emailService.sendOrderConfirmationEmail(user.getEmail(), savedOrder);
        } catch (Exception e) {
            System.err.println("Could not send order confirmation email: " + e.getMessage());
        }

        return savedOrder;
    }

    public List<OrderDto> getOrdersByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Order> orders = orderRepo.findByUser(user);

        return orders.stream()
                .map(orderDtoMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        return orderRepo.findAll().stream()
                .map(orderDtoMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
