package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.ProductDto;
import com.scrable.bitirme.dto.ProductDtoMapper;
import com.scrable.bitirme.dto.WishlistRequestDto;
import com.scrable.bitirme.exception.ProductNotFoundException;
import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.model.Wishlist;
import com.scrable.bitirme.repository.ProductRepo;
import com.scrable.bitirme.repository.UserRepo;
import com.scrable.bitirme.repository.WishlistRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepo wishlistRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final ProductDtoMapper productDtoMapper;


    public void addProductToWishlist(WishlistRequestDto request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.getProductId()));

        Optional<Wishlist> wishlistItem = wishlistRepo.findByUserAndProduct(user, product);
        if (wishlistItem.isPresent()) {
            return;
        }

        Wishlist newWishlistItem = new Wishlist(user, product);
        wishlistRepo.save(newWishlistItem);
    }

    @Transactional
    public void removeProductFromWishlist(WishlistRequestDto request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.getProductId()));

        Optional<Wishlist> wishlistItem = wishlistRepo.findByUserAndProduct(user, product);

        wishlistItem.ifPresent(wishlistRepo::delete);
    }

    public List<ProductDto> getWishlistByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Wishlist> wishlistItems = wishlistRepo.findByUser(user);

        return wishlistItems.stream()
                .map(Wishlist::getProduct)
                .map(productDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}