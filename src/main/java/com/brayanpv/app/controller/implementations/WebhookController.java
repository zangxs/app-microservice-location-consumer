package com.brayanpv.app.controller.implementations;

import com.brayanpv.app.controller.contracts.IWebhookController;
import com.brayanpv.app.model.request.TelegramUpdate;
import com.brayanpv.app.model.response.ApiResponse;
import com.brayanpv.app.service.contracts.IConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/webhook")
public class WebhookController implements IWebhookController {

    private final IConsumerService consumerService;

    @Override
    @PostMapping("/telegram")
    public Mono<ResponseEntity<ApiResponse>> handleTelegramWebhook(@RequestBody TelegramUpdate update) {

        log.info("Webhook received: {}", update);

        if (update.callbackQuery() == null) {
            return Mono.just(ResponseEntity.ok(ApiResponse.builder()
                    .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                    .code(200)
                    .data("No callback query")
                    .build()));
        }

        return consumerService.processCallback(update.callbackQuery().data())
                .thenReturn(ResponseEntity.ok(ApiResponse.builder()
                        .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                        .code(200)
                        .data("Processed")
                        .build()));
    }
}
