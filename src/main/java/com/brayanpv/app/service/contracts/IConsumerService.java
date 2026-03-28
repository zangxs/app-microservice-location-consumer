package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.message.LandscapeEvent;
import com.brayanpv.app.model.response.ApiResponse;
import reactor.core.publisher.Mono;

public interface IConsumerService {

    void consume(LandscapeEvent event);
    Mono<ApiResponse> processCallback(String data);
}
