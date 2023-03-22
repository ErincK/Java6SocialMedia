package com.erinc.service;

import com.erinc.rabbitmq.model.RegisterMailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender javaMailSender;



    public void sendMail(RegisterMailModel model){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("${JAVA6_MAIL_USERNAME}");
        mailMessage.setTo(model.getEmail());
        mailMessage.setSubject("AKTIVASYON KODU...: ");
        mailMessage.setText(model.getUsername()+" adıyla başarılı bir şekilde kayıt olmuştur.\n" + "Aktivasyon Kodunuz..: " + model.getActivationCode());
        javaMailSender.send(mailMessage);
    }


}
