package com.brayanpv.app.service.implementations;

import com.brayanpv.app.service.contracts.IRabbitMQService;
import com.brayanspv.library.model.events.LandscapeEvent;
import com.brayanspv.library.model.events.LandscapeStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
@RequiredArgsConstructor
public class RabbitMQService implements IRabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.status-routing-key}")
    private String statusRoutingKey;

    @Override
    public Mono<Void> publishStatusEvent(LandscapeStatusEvent event) {
        return Mono.fromCallable(() -> {
                    rabbitTemplate.convertAndSend(exchange, statusRoutingKey, event);
                    log.info("Event published to RabbitMQ: {}", event.landscapeId());
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
