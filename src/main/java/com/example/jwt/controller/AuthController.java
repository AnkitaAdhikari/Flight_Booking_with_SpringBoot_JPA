package com.example.jwt.controller;

import com.example.jwt.dto.*;
import com.example.jwt.service.AuthService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		System.out.println("Received signup request: " + request);
		String token = authService.register(request);
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		String token = authService.login(request);
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}
}
