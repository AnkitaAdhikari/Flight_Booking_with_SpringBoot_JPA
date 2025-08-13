package com.example.jwt.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.entity.User;
import com.example.jwt.repository.UserRepository;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authManager;

	public String register(RegisterRequest request) {
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole("ROLE_" + request.getRole().toUpperCase());
		user.setEnabled(false); // account disabled until verified

		String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit code
		user.setVerificationCode(code);
		user.setVerificationExpiry(LocalDateTime.now().plusMinutes(10));

		userRepository.save(user);

		// Email

		emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);

		// ✅ Generate token using email
		return jwtUtil.generateToken(user.getEmail());
	}

	public String login(LoginRequest request) {
		// ✅ Authenticate using email + password
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		// ✅ Return token using email (as subject)
		return jwtUtil.generateToken(request.getEmail());
	}
}
