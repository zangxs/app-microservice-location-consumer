package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.request.TelegramMessage;
import reactor.core.publisher.Mono;

public interface ITelegramService {
    Mono<Void> sendPhoto(TelegramMessage message);
}
