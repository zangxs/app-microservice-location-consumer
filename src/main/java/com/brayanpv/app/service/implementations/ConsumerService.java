package com.brayanpv.app.service.implementations;

import com.brayanpv.app.component.mapper.LandscapeEventMapper;
import com.brayanpv.app.model.message.LandscapeEvent;
import com.brayanpv.app.model.request.TelegramMessage;
import com.brayanpv.app.service.contracts.IConsumerService;
import com.brayanpv.app.service.contracts.ITelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConsumerService implements IConsumerService {

    private final LandscapeEventMapper mapper;
    private final ITelegramService telegramService;

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
}
