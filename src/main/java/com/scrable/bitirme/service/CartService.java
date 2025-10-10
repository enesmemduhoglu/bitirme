package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.CartRequest;
import com.scrable.bitirme.dto.CartResponse;
import com.scrable.bitirme.model.Cart;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.repository.CartRepo;
import com.scrable.bitirme.repository.ProductRepo;
import com.scrable.bitirme.repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    public List<CartResponse> getCartByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepo.findByUser(user)
                .stream()
                .map(cart -> {
                    CartResponse response = new CartResponse();
                    response.setUserId(cart.getUser().getId());
                    response.setProductId(cart.getProduct().getProductId());
                    response.setQuantity(cart.getQuantity());

                    response.setProductName(cart.getProduct().getProductName());
                    response.setProductImage(cart.getProduct().getProductImage());
                    response.setProductPrice(cart.getProduct().getProductPrice());
                    response.setProductDescription(cart.getProduct().getProductDescription());

                    return response;
                })
                .collect(Collectors.toList());
    }

    public void addProductToCart(CartRequest cartRequest) {
       User user = userRepo.findById(cartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

       Product product = productRepo.findById(cartRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> existingCartItem = cartRepo.findByUserAndProduct(user, product);

        if (existingCartItem.isPresent()) {
            Cart cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartRequest.getQuantity());
            cartRepo.save(cartItem);
        } else {
            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartRequest.getQuantity());
            cartRepo.save(cartItem);
        }

    }




}
