package com.brayanpv.app.service.contracts;

import com.brayanspv.library.model.events.LandscapeEvent;
import com.brayanspv.library.model.events.LandscapeStatusEvent;
import reactor.core.publisher.Mono;

public interface IRabbitMQService {
    Mono<Void> publishStatusEvent(LandscapeStatusEvent event);
}
