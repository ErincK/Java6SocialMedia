package com.erinc.rabbitmq.consumer;

import com.erinc.mapper.IUserMapper;
import com.erinc.rabbitmq.model.RegisterModel;
import com.erinc.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // Console'a log info çıkmak için kullanılan kütüphane
public class RegisterConsumer {

    private final UserProfileService userProfileService;

    @RabbitListener(queues = ("${rabbitmq.queueRegister}"))
    public void newUserCreate(RegisterModel model){

        log.info("User {}",model.toString());
        userProfileService.createUserWithRabbitMq(model);
        //userProfileService.createUser(IUserMapper.INSTANCE.toNewCreateUserRequestDto(model));    ## 2. TERCİH ##
    }






}






