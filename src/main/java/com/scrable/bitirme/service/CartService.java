package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.CartRequest;
import com.scrable.bitirme.dto.CartResponse;
import com.scrable.bitirme.exception.CartLimitExceededException;
import com.scrable.bitirme.exception.ProductNotFoundException;
import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.Cart;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.repository.CartRepo;
import com.scrable.bitirme.repository.ProductRepo;
import com.scrable.bitirme.repository.UserRepo;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

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
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + cartRequest.getUserId()));

        Product product = productRepo.findById(cartRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + cartRequest.getProductId()));

        Optional<Cart> existingCartItem = cartRepo.findByUserAndProduct(user, product);

        Integer maxQuantity = product.getMaxQuantityPerCart();

        if (existingCartItem.isPresent()) {
            Cart cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + cartRequest.getQuantity();

            if (maxQuantity != null && newQuantity > maxQuantity) {
                throw new CartLimitExceededException("You can add a maximum of " + maxQuantity + " of this product to your cart.");
            }

            cartItem.setQuantity(newQuantity);
            cartRepo.save(cartItem);
        } else {
            if (maxQuantity != null && cartRequest.getQuantity() > maxQuantity) {
                throw new CartLimitExceededException("You can add a maximum of " + maxQuantity + " of this product to your cart.");
            }

            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartRequest.getQuantity());
            cartRepo.save(cartItem);
        }
    }

}
