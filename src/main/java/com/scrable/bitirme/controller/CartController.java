package com.scrable.bitirme.controller;

import com.scrable.bitirme.dto.CartRequest;
import com.scrable.bitirme.dto.CartResponse;
import com.scrable.bitirme.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addProductToCart(@RequestBody CartRequest cartRequest) {
        cartService.addProductToCart(cartRequest);
        return ResponseEntity.ok("Product added to cart successfully.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponse>> getCart(@PathVariable Long userId) {
        List<CartResponse> cartItems = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeProductFromCart(@RequestBody CartRequest cartRequest) {
        cartService.removeProductFromCart(cartRequest);
        return ResponseEntity.ok().build();
    }

}
