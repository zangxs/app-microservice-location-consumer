package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.TelegramMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ClientResponse clientResponse;

    private TelegramService telegramService;

    private TelegramMessage testMessage;

    @BeforeEach
    void setUp() {
        telegramService = new TelegramService();
        ReflectionTestUtils.setField(telegramService, "webClient", webClient);
        ReflectionTestUtils.setField(telegramService, "chatId", "12345");
        ReflectionTestUtils.setField(telegramService, "botToken", "test-token");

        testMessage = new TelegramMessage(
                "landscape-1",
                "https://example.com/image.jpg",
                "Test Title",
                "Test Description",
                -12.0464,
                -77.0428
        );
    }

    @Test
    void sendPhoto_success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/bottest-token/sendPhoto")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        Mono<Void> result = telegramService.sendPhoto(testMessage);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void sendPhoto_error() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/bottest-token/sendPhoto")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("Telegram error")));

        Mono<Void> result = telegramService.sendPhoto(testMessage);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void sendLocation_success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/bottest-token/sendLocation")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        Mono<Void> result = telegramService.sendLocation(testMessage);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void sendLocation_error() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/bottest-token/sendLocation")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("Telegram error")));

        Mono<Void> result = telegramService.sendLocation(testMessage);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void sendApprovalButtons_success() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/bottest-token/sendMessage")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        Mono<Void> result = telegramService.sendApprovalButtons(testMessage);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void sendApprovalButtons_error() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/bottest-token/sendMessage")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("Telegram error")));

        Mono<Void> result = telegramService.sendApprovalButtons(testMessage);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
