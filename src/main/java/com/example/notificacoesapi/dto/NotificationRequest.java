package com.example.notificacoesapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NotificationRequest(

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Mensagem é obrigatória")
    String mensagem,

    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(
        regexp = "EMAIL|SMS|PUSH",
        message = "Tipo deve ser EMAIL, SMS ou PUSH"
    )
    String tipo
) {

}