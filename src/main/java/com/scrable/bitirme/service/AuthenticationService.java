package com.scrable.bitirme.service;

import com.scrable.bitirme.model.AuthenticationResponse;
import com.scrable.bitirme.model.Role;
import com.scrable.bitirme.model.Token;
import com.scrable.bitirme.model.Users;
import com.scrable.bitirme.repository.TokenRepo;
import com.scrable.bitirme.exception.EmailAlreadyExistsException;
import com.scrable.bitirme.exception.UserNotVerifiedException;
import com.scrable.bitirme.repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<String> register(Users request) {
        if (usersRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Username already exists");
        }

        if (usersRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        if (request.getPassword().length() < 8) {
            throw new ResponseStatusException(BAD_REQUEST, "Password must be at least 8 characters");
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setEnabled(false);
        user.setVerificationCode(UUID.randomUUID().toString());

        usersRepo.save(user);

        emailService.sendVerificationEmail(user);

        log.info("User registered. Verification email sent to {}", user.getEmail());
        return ResponseEntity.status(CREATED)
                .body("Registration successful. Please verify your email before logging in.");
    }


    public AuthenticationResponse authenticateUser(Users request) {
        return authenticate(request, Role.USER);
    }


    public AuthenticationResponse authenticateAdmin(Users request) {
        return authenticate(request, Role.ADMIN);
    }


    private AuthenticationResponse authenticate(Users request, Role expectedRole) {
        Users user = usersRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        if (user.getRole() != expectedRole) {
            throw new ResponseStatusException(FORBIDDEN, "Unauthorized access");
        }

        if (!user.isEnabled()) {
            throw new UserNotVerifiedException("Email not verified");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllTokensByUser(user);
        saveUserToken(user, accessToken, refreshToken);

        log.info("User '{}' authenticated successfully", user.getUsername());
        return new AuthenticationResponse(accessToken, refreshToken);
    }


    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(UNAUTHORIZED, "Refresh token is missing or invalid");
        }

        final String refreshToken = authHeader.substring(7);
        final String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Username not found in token");
        }

        Users user = usersRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        if (!jwtService.isValidRefreshToken(refreshToken, user)) {
            throw new ResponseStatusException(FORBIDDEN, "Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        revokeAllTokensByUser(user);
        saveUserToken(user, newAccessToken, newRefreshToken);

        log.info("Refresh token used successfully by {}", username);
        return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, newRefreshToken));
    }


    private void revokeAllTokensByUser(Users user) {
        List<Token> validTokens = tokenRepo.findAllAccessTokensByUser(user.getId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(token -> token.setLoggedOut(true));
        tokenRepo.saveAll(validTokens);
    }


    private void saveUserToken(Users user, String accessToken, String refreshToken) {
        Token token = new Token();
        token.setUsers(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        tokenRepo.save(token);
    }
}


