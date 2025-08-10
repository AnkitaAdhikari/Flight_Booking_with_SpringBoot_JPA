package com.example.jwt.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RegisterRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	private String role;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
}
