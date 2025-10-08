package com.scrable.bitirme.dto;

import com.scrable.bitirme.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsersDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Role role;
}
