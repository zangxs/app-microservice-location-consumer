package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.response.ApiResponse;
import com.brayanspv.library.model.events.LandscapeEvent;
import reactor.core.publisher.Mono;

public interface IConsumerService {

    void consume(LandscapeEvent event);
    Mono<ApiResponse> processCallback(String data);
}
