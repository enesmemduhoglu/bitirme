package com.scrable.bitirme.service;

import com.scrable.bitirme.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(Users user) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/verify?code=" + user.getVerificationCode();
        String message = "Please verify your email by clicking the following link: " + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
