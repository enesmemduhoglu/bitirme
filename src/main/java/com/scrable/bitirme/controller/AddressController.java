package com.scrable.bitirme.controller;

import com.scrable.bitirme.model.Address;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.repository.AddressRepo;
import com.scrable.bitirme.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;

    @GetMapping
    public ResponseEntity<List<Address>> getMyAddresses() {
        User user = getCurrentUser();
        return ResponseEntity.ok(addressRepo.findByUserId(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address address) {
        User user = getCurrentUser();
        address.setUser(user);

        // If this is the first address, make it default
        List<Address> existing = addressRepo.findByUserId(user.getId());
        if (existing.isEmpty()) {
            address.setDefault(true);
        }

        return ResponseEntity.ok(addressRepo.save(address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        User user = getCurrentUser();
        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (address.getUser().getId() != user.getId()) {
            throw new RuntimeException("Not authorized");
        }

        addressRepo.delete(address);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
