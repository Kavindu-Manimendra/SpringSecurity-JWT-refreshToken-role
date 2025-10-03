package com.example.demo.services.impl;

import com.example.demo.dtos.*;
import com.example.demo.entities.RefreshToken;
import com.example.demo.entities.User;
import com.example.demo.enums.Role;
import com.example.demo.repo.RefreshTokenRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.services.AuthenticationService;
import com.example.demo.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RefreshTokenRepo refreshTokenRepo;

    public JwtAuthenticationResponse signup(SignUpRequest signUpRequest) {
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.USER);

        User newUser = userRepo.save(user);

        UserDto userDto = new UserDto();
        userDto.setId(newUser.getId().toString());

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setMessage("Registration Successful");
        response.setUserDto(userDto);
        return response;
    }

    public JwtAuthenticationResponse signin(SignInRequest signinRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signinRequest.getEmail(), signinRequest.getPassword()));

        var user = userRepo.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password!"));

        var jwt = jwtService.generateToken(user, user.getId());
        var refreshToken = jwtService.generateRefreshToken(user);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);

        UserDto userDto = new UserDto();
        userDto.setId(user.getId().toString());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRole().toString().split(","));
        jwtAuthenticationResponse.setUserDto(userDto);
        return jwtAuthenticationResponse;
    }

    public JwtAuthenticationResponse genarateNewTokenUsingRefreshToken(NewTokenRequest newTokenRequest) throws Exception {
        RefreshToken refreshToken = refreshTokenRepo.findByRefreshToken(newTokenRequest.getRefreshToken()).get();
        if (jwtService.isRefreshTokenExpired(refreshToken)) {
            refreshTokenRepo.delete(refreshToken);
            throw new Exception("Refresh token was expired. Please make a new signin request");
        }
        var jwt = jwtService.generateToken(refreshToken.getUser(), refreshToken.getUser().getId());

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setMessage("New access token created!!!");
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(newTokenRequest.getRefreshToken());

        User user = refreshToken.getUser();
        UserDto userDto = new UserDto();
        userDto.setId(user.getId().toString());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRole().toString().split(","));
        jwtAuthenticationResponse.setUserDto(userDto);

        return jwtAuthenticationResponse;

    }

    @Transactional
    public JwtAuthenticationResponse logout(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken).get();
        refreshTokenRepo.delete(refreshTokenEntity);

        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setMessage("Refresh token has been deleted.");
        return response;
    }
}
