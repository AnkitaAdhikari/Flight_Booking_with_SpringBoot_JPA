package com.example.jwt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendVerificationCode(String to, String username, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // taken from application.properties
        message.setTo(to);
        message.setSubject("Verify Your Account");
        message.setText("Hello " + username + ",\n\n"
                + "Thank you for signing up! Please use the following verification code to activate your account:\n\n"
                + code + "\n\n"
                + "This code will expire in 10 minutes.\n\n"
                + "Regards,\nYour Team");

        mailSender.send(message);
    }
}
