package com.example.notificacoesapi.service;

import com.example.notificacoesapi.dto.NotificationRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducerService {
 
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public NotificationProducerService(
        RabbitTemplate rabbitTemplate,
        @Value("${app.rabbitmq.exchange}") String exchangeName,
        @Value("${app.rabbitmq.routingkey}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public void send(NotificationRequest request) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, request);
    }
}
