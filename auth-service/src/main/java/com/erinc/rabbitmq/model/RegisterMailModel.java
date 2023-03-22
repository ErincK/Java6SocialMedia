package com.erinc.rabbitmq.model;

import lombok.*;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMailModel implements Serializable {


    private String username;
    private String email;
    private String activationCode;
}
