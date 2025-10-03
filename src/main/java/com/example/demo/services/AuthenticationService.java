package com.example.demo.services;

import com.example.demo.dtos.JwtAuthenticationResponse;
import com.example.demo.dtos.NewTokenRequest;
import com.example.demo.dtos.SignInRequest;
import com.example.demo.dtos.SignUpRequest;
import com.example.demo.entities.User;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest signUpRequest);
    JwtAuthenticationResponse signin(SignInRequest signinRequest);
    JwtAuthenticationResponse genarateNewTokenUsingRefreshToken(NewTokenRequest newTokenRequest) throws Exception;
    JwtAuthenticationResponse logout(String refreshToken);
}
