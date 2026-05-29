package com.example.notificacoesapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Mensagem é obrigatória")
    String mensagem,

    @NotNull(message = "Tipo é obrigatório")
    NotificationType tipo
) {

}