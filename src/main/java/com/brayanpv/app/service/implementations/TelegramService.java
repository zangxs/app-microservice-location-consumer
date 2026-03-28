package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.TelegramMessage;
import com.brayanpv.app.service.contracts.ITelegramService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class TelegramService implements ITelegramService {
    @Override
    public Mono<Void> sendPhoto(TelegramMessage message) {
        return null;
    }
}
