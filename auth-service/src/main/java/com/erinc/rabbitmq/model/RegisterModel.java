package com.erinc.rabbitmq.model;

import lombok.*;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RegisterModel implements Serializable {

    private Long authId;
    private String username;
    private String email;

}
