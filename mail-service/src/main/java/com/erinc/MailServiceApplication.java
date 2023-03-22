package com.erinc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
public class MailServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailServiceApplication.class);
    }

//    private final JavaMailSender javaMailSender;
//
//
//    public MailServiceApplication(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void sendMail(){
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom("${JAVA6_MAIL_USERNAME}");
//        mailMessage.setTo("erinckocaoglu00@gmail.com");
//        mailMessage.setSubject("AKTIVASYON KODU...: ");
//        mailMessage.setText("ASD1313");
//        javaMailSender.send(mailMessage);
//    }




}