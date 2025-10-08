package com.scrable.bitirme.dto;

import com.scrable.bitirme.model.Users;
import org.springframework.stereotype.Component;

@Component
public class UsersDtoMapper {

    public UsersDto toDto(Users users) {
        return new UsersDto(
                users.getId(),
                users.getFirstName(),
                users.getLastName(),
                users.getUsername(),
                users.getRole()
        );
    }

    public void updateUserFromDto(UsersDto usersDto, Users users) {
        if (usersDto.getFirstName() != null) {
            users.setFirstName(usersDto.getFirstName());
        }
        if (usersDto.getLastName() != null) {
            users.setLastName(usersDto.getLastName());
        }
        if (usersDto.getUsername() != null) {
            users.setUsername(usersDto.getUsername());
        }
        if (usersDto.getRole() != null) {
            users.setRole(usersDto.getRole());
        }
    }

}
