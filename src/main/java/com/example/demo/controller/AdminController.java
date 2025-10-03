package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

    @GetMapping
    public ResponseEntity<String> getAdmin() {
        return ResponseEntity.ok("Hello, This is admin endpoint. Only admin role can access this.");
    }
}
