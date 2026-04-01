package com.brayanpv.app.service.implementations;

import com.brayanspv.library.model.events.LandscapeStatusEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQService rabbitMQService;

    @BeforeEach
    void setUp() {
        rabbitMQService = new RabbitMQService(rabbitTemplate);
        ReflectionTestUtils.setField(rabbitMQService, "exchange", "landscape.exchange");
        ReflectionTestUtils.setField(rabbitMQService, "statusRoutingKey", "landscape.status");
    }

    @Test
    void publishStatusEvent_success() {
        LandscapeStatusEvent event = new LandscapeStatusEvent("landscape-123", "APPROVED");

        Mono<Void> result = rabbitMQService.publishStatusEvent(event);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void publishStatusEvent_withRejectedStatus() {
        LandscapeStatusEvent event = new LandscapeStatusEvent("landscape-456", "REJECTED");

        Mono<Void> result = rabbitMQService.publishStatusEvent(event);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
