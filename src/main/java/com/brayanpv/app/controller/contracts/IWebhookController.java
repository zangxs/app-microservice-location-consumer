package com.brayanpv.app.controller.contracts;

import com.brayanpv.app.model.request.TelegramUpdate;
import com.brayanpv.app.model.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface IWebhookController {
    Mono<ResponseEntity<ApiResponse>> handleTelegramWebhook(@RequestBody TelegramUpdate update);
}
