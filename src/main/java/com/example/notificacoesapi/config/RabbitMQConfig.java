package com.example.notificacoesapi.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    @Value("${app.rabbitmq.dlx.exchange}")
    private String dlxExchangeName;

    @Value("${app.rabbitmq.dlq}")
    private String dlqName;

    @Value("${app.rabbitmq.dlq.routingkey}")
    private String dlqRoutingkey;

    @Value("${app.rabbitmq.retry.max-attempts}")
    private int retryMaxAttempts;

    @Value("${app.rabbitmq.retry.initial-interval}")
    private long retryInitialInterval;

    @Value("${app.rabbitmq.retry.multiplier}")
    private double retryMultiplier;

    @Value("${app.rabbitmq.retry.max-interval}")
    private long retryMaxInterval;

    @Bean
    public Queue notificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", dlxExchangeName);
        args.put("x-dead-letter-routing-key", dlqRoutingkey);
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(routingKey);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(dlqName, true);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxExchangeName);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(dlqRoutingkey);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory,
        RabbitTemplate rabbitTemplate) { 
    
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());

        RepublishMessageRecoverer recoverer =
            new RepublishMessageRecoverer(rabbitTemplate, dlxExchangeName, dlqRoutingkey);

            factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                    .maxAttempts(retryMaxAttempts)
                    .backOffOptions(retryInitialInterval, retryMultiplier, retryMaxInterval)
                    .recoverer(recoverer)
                    .build()
            );
        return factory;
    }
}