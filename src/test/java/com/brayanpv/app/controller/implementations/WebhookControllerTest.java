package com.brayanpv.app.controller.implementations;

import com.brayanpv.app.model.request.CallbackQuery;
import com.brayanpv.app.model.request.TelegramUpdate;
import com.brayanpv.app.model.response.ApiResponse;
import com.brayanpv.app.service.contracts.IConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private IConsumerService consumerService;

    @InjectMocks
    private WebhookController controller;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void handleTelegramWebhook_withNoCallbackQuery_returnsOk() {
        TelegramUpdate update = new TelegramUpdate(1L, null);

        webTestClient.post()
                .uri("/webhook/telegram")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").isEqualTo("No callback query");
    }

    @Test
    void handleTelegramWebhook_withCallbackQuery_callsProcessCallback() {
        CallbackQuery callback = new CallbackQuery("callback-1", "APPROVE:landscape-123");
        TelegramUpdate update = new TelegramUpdate(2L, callback);

        ApiResponse mockResponse = ApiResponse.builder()
                .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                .code(200)
                .data("Processed")
                .build();

        when(consumerService.processCallback("APPROVE:landscape-123"))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.post()
                .uri("/webhook/telegram")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").isEqualTo("Processed");
    }

    @Test
    void handleTelegramWebhook_withRejectCallback_callsProcessCallback() {
        CallbackQuery callback = new CallbackQuery("callback-2", "REJECT:landscape-456");
        TelegramUpdate update = new TelegramUpdate(3L, callback);

        ApiResponse mockResponse = ApiResponse.builder()
                .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                .code(200)
                .data("Processed")
                .build();

        when(consumerService.processCallback("REJECT:landscape-456"))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.post()
                .uri("/webhook/telegram")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(200)
                .jsonPath("$.data").isEqualTo("Processed");
    }

    @Test
    void handleTelegramWebhook_reactive_returnsMonoResponseEntity() {
        CallbackQuery callback = new CallbackQuery("callback-3", "APPROVE:test-789");
        TelegramUpdate update = new TelegramUpdate(4L, callback);

        ApiResponse mockResponse = ApiResponse.builder()
                .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                .code(200)
                .data("Processed")
                .build();

        when(consumerService.processCallback("APPROVE:test-789"))
                .thenReturn(Mono.just(mockResponse));

        Mono<org.springframework.http.ResponseEntity<ApiResponse>> result = controller.handleTelegramWebhook(update);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assert response.getStatusCode().is2xxSuccessful();
                    assert response.getBody() != null;
                    assert response.getBody().getCode() == 200;
                    assert response.getBody().getData().equals("Processed");
                })
                .verifyComplete();
    }
}
