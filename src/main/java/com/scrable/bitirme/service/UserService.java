package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.UserDto;
import com.scrable.bitirme.dto.UserDtoMapper;
import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.repository.TokenRepo;
import com.scrable.bitirme.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final UserDtoMapper userDtoMapper;

    public List<UserDto> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userDtoMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto updateUserDto) {
        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userDtoMapper.updateUserFromDto(updateUserDto, existingUser);
        User updatedUser = userRepo.save(existingUser);

        return userDtoMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        tokenRepo.deleteByUserId(id);
        userRepo.deleteById(id);
    }

    public String verifyUser(String verificationCode) {
        User user = userRepo.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new UserNotFoundException("Invalid verification code."));

        if (user.isEnabled()) {
            return "Account is already verified.";
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepo.save(user);

        return "Account verified successfully.";
    }
}
