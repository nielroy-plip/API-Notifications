package com.example.notificacoesapi.controller;

import com.example.notificacoesapi.dto.NotificationRequest;
import com.example.notificacoesapi.service.NotificationProducerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificationController {
    
    private final NotificationProducerService producerService;

    public NotificationController(NotificationProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<String> enviar(@Valid @RequestBody NotificationRequest request) {
        producerService.send(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body("Notificação recebida e enviada para fila com sucesso.");
    }
}
