package com.erinc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDto {
    @NotBlank(message = "Kullanıcı adı boş geçilemez")

    @Size(min = 3,max=20,message = "Kullanıcı adı en az 3 karakter en fazla 20 karakter olmalıdır")
    private String username;
    @Email
    private String email;
    @NotBlank
    @Size(min =5,max=32,message = "Şifre  en az 5 karakter en fazla 32 karakter olmalıdır")
    private String password;
}
