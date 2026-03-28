package com.brayanpv.app.service.implementations;

import com.brayanpv.app.component.mapper.LandscapeEventMapper;
import com.brayanpv.app.model.message.LandscapeEvent;
import com.brayanpv.app.model.request.TelegramMessage;
import com.brayanpv.app.model.response.ApiResponse;
import com.brayanpv.app.repositories.contracts.ILandscapeRepository;
import com.brayanpv.app.service.contracts.IConsumerService;
import com.brayanpv.app.service.contracts.ITelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConsumerService implements IConsumerService {

    private final LandscapeEventMapper mapper;
    private final ITelegramService telegramService;
    private final ILandscapeRepository  landscapeRepository;

    @Override
    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consume(LandscapeEvent event) {
        log.info("Event received: {}", event.landscapeId());
        TelegramMessage message = mapper.toTelegramMessage(event);

        telegramService.sendPhoto(message)
                .then(telegramService.sendLocation(message))
                .then(telegramService.sendApprovalButtons(message))
                .subscribe();
    }

    @Override
    public Mono<ApiResponse> processCallback(String data) {

        String[] parts = data.split(":");
        String action = parts[0];
        String landscapeId = parts[1];

        log.info("Processing callback: action={}, landscapeId={}", action, landscapeId);

        String status = action.equals("APPROVE") ? "APPROVED" : "REJECTED";

        return landscapeRepository.updateStatus(status, landscapeId)
                .thenReturn(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data(landscapeId)
                        .build());

    }
}
