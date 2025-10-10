package com.scrable.bitirme.dto;

import com.scrable.bitirme.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;

        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRole()
        );
    }

    public void updateUserFromDto(UserDto userDto, User user) {
        if (userDto == null || user == null) return;

        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
    }

}
