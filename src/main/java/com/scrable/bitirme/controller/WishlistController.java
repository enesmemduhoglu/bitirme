package com.scrable.bitirme.controller;

import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.dto.WishlistRequestDto;
import com.scrable.bitirme.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add")
    public ResponseEntity<String> addProductToWishlist(@RequestBody WishlistRequestDto request) {
        wishlistService.addProductToWishlist(request);
        return ResponseEntity.ok("Product successfully added to wishlist.");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeProductFromWishlist(@RequestBody WishlistRequestDto request) {
        wishlistService.removeProductFromWishlist(request);
        return ResponseEntity.ok("Product successfully removed from wishlist.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ProductDto>> getWishlist(@PathVariable Long userId) {
        List<ProductDto> wishlistProducts = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.ok(wishlistProducts);
    }
}