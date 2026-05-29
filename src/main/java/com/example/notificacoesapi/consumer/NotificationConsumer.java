package com.example.notificacoesapi.consumer;

import com.example.notificacoesapi.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receive(NotificationRequest request) {
        if (request.tipo() == null) {
            throw new IllegalArgumentException("Tipo de notificação não informado");
        }

        log.info("=== Mensagem recebida da fila ===");
        log.info("Email : {}", request.email());
        log.info("Tipo : {}", request.tipo());
        log.info("Mensagem : {}", request.mensagem());

        simularEnvio(request);
    }

    private void simularEnvio(NotificationRequest request) {
        switch (request.tipo()) {
            case EMAIL -> log.info("[EMAIL] Enviando email para {}...", request.email());
            case SMS -> log.info("[SMS] Enviando SMS para {}...", request.email());
            case PUSH -> log.info("[PUSH] Enviando push notification para {}...", request.email());
            default -> log.warn("[AVISO] Tipo de notificação desconhecido: {}", request.tipo());
        }
        log.info("=== Notificação processada com sucesso ===");
    }
}
