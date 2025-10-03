package com.example.demo.dtos;

import com.example.demo.enums.Role;
import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String[] roles;
}
