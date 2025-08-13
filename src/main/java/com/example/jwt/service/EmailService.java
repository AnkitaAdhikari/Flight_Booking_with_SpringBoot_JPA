package com.example.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Value("${app.mail.from}")
	private String fromEmail;

	@Autowired
	private JavaMailSender mailSender;

	public void sendSignupSuccessEmail(String to, String username) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(to);
		message.setSubject("Signup Successful");
		message.setText("Hello " + username + ",\n\nYour signup was successful!\n\nWelcome aboard.\n\nRegards,\nTeam");
		mailSender.send(message);
	}
}
