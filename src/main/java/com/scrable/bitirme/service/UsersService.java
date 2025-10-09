package com.scrable.bitirme.service;

import com.scrable.bitirme.dto.UsersDto;
import com.scrable.bitirme.dto.UsersDtoMapper;
import com.scrable.bitirme.exception.UserNotFoundException;
import com.scrable.bitirme.model.Users;
import com.scrable.bitirme.repository.TokenRepo;
import com.scrable.bitirme.repository.UsersRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepo usersRepo;
    private final TokenRepo tokenRepo;
    private final UsersDtoMapper usersDtoMapper;

    public List<UsersDto> getAllUsers() {
        return usersRepo.findAll()
                .stream()
                .map(usersDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsersDto updateUser(Long id, UsersDto updateUserDto) {
        Users existingUser = usersRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        usersDtoMapper.updateUserFromDto(updateUserDto, existingUser);
        Users updatedUser = usersRepo.save(existingUser);

        return usersDtoMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!usersRepo.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        tokenRepo.deleteByUsersId(id);
        usersRepo.deleteById(id);
    }

    public String verifyUser(String verificationCode) {
        Users user = usersRepo.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new UserNotFoundException("Invalid verification code."));

        if (user.isEnabled()) {
            return "Account is already verified.";
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        usersRepo.save(user);

        return "Account verified successfully.";
    }
}
