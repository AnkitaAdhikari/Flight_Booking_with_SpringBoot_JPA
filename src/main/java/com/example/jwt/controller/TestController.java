package com.example.jwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminOnly() {
		return "Hello Admin!";
	}

	@GetMapping("/user")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String userAccess() {
		return "Hello User!";
	}
}
