package com.scrable.bitirme.controller;

import com.scrable.bitirme.model.AuthenticationResponse;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.service.AuthenticationService;
import com.scrable.bitirme.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService usersService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    @PostMapping("/admin")
    public ResponseEntity<AuthenticationResponse> admin(@RequestBody User request) {
        return ResponseEntity.ok(authService.authenticateAdmin(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String code) {
        String verificationResult = usersService.verifyUser(code);

        if (verificationResult.equals("Account verified successfully.")) {
            return ResponseEntity.ok(verificationResult);
        }
        else  {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(verificationResult);
        }
    }

}

