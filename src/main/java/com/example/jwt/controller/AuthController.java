package com.example.jwt.controller;

import com.example.jwt.dto.*;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.service.AuthService;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		System.out.println("Received signup request: " + request);
		String token = authService.register(request);
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verifyAccount(@RequestBody VerificationRequest request) {
		Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
		if (!userOpt.isPresent()) {
			return ResponseEntity.badRequest().body("Invalid email.");
		}

		User user = userOpt.get();
		if (user.getVerificationCode() != null && user.getVerificationCode().equals(request.getCode())) {
			user.setEnabled(true);
			user.setVerificationCode(null); // clear code
			userRepository.save(user);
			return ResponseEntity.ok("Account verified successfully!");
		} else {
			return ResponseEntity.badRequest().body("Invalid verification code.");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		String token = authService.login(request);
		return ResponseEntity.ok(Collections.singletonMap("token", token));
	}
}
