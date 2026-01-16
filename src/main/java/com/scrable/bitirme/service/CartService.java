package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.AddToCartResponse;
import com.scrable.bitirme.dto.CartRequest;
import com.scrable.bitirme.dto.CartResponse;
import com.scrable.bitirme.exception.OutOfStockException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final FileStorageService fileStorageService;

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
                    String presignedUrl = fileStorageService.generatePresignedUrl(cart.getProduct().getProductImage());
                    response.setProductImage(presignedUrl);
                    response.setProductPrice(cart.getProduct().getProductPrice());
                    response.setProductDescription(cart.getProduct().getProductDescription());

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public AddToCartResponse addProductToCart(CartRequest cartRequest) {
        if (cartRequest.getQuantity() == null || cartRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("The quantity of the product you want to update cannot be zero.");
        }

        User user = userRepo.findById(cartRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + cartRequest.getUserId()));

        Product product = productRepo.findById(cartRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + cartRequest.getProductId()));

        Integer stock = product.getProductStock();
        if (stock == null || stock <= 0) {
            throw new OutOfStockException("Product " + product.getProductName() + " is out of stock");
        }

        Optional<Cart> existingCartItem = cartRepo.findByUserAndProduct(user, product);

        int currentQuantity = existingCartItem.map(Cart::getQuantity).orElse(0);
        int requestedQuantity = cartRequest.getQuantity();
        int potentialTotalQuantity = currentQuantity + requestedQuantity;

        Integer maxPerCart = product.getMaxQuantityPerCart();

        // Bu kısım önemli, eğer maxPerCart null ise sadece stock'a bakar.
        int effectiveLimit = stock;
        if (maxPerCart != null && maxPerCart < stock) {
            effectiveLimit = maxPerCart;
        }

        String message;
        int finalQuantity;

        // Eğer istenen miktar stoktan veya maxPerCart'tan fazlaysa, limitler.
        if (potentialTotalQuantity > effectiveLimit) {
            finalQuantity = effectiveLimit;

            if (effectiveLimit == stock) {
                message = "Only " + stock + " units are available in stock.";
            } else {
                message = "You can add up to " + maxPerCart + " units of this product.";
            }
        } else {
            finalQuantity = potentialTotalQuantity;
            message = "The product has been successfully added to your cart.";
        }

        Cart cartItem = existingCartItem.orElse(new Cart());
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(finalQuantity);
        cartRepo.save(cartItem);

        int actualQuantityAdded = finalQuantity - currentQuantity;

        return new AddToCartResponse(message, actualQuantityAdded, product.getProductId());
    }

    @Transactional
    public void removeProductFromCart(CartRequest cartRequest) {
        User user = userRepo.findById(cartRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + cartRequest.getUserId()));

        Product product = productRepo.findById(cartRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + cartRequest.getProductId()));

        Cart cartItem = cartRepo.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ProductNotFoundException("Cart item not found"));

        cartRepo.delete(cartItem);
    }

}
