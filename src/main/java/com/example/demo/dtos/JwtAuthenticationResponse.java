package com.example.demo.dtos;

import lombok.Data;

@Data
public class JwtAuthenticationResponse {
    private String message;
    private String token;
    private String refreshToken;

    private UserDto userDto;
}
