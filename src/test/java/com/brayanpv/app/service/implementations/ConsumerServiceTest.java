package com.brayanpv.app.service.implementations;

import com.brayanpv.app.component.mapper.LandscapeEventMapper;
import com.brayanpv.app.model.request.TelegramMessage;
import com.brayanpv.app.model.response.ApiResponse;
import com.brayanpv.app.service.contracts.IRabbitMQService;
import com.brayanpv.app.service.contracts.ITelegramService;
import com.brayanspv.library.model.events.LandscapeEvent;
import com.brayanspv.library.model.events.LandscapeStatusEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private LandscapeEventMapper mapper;

    @Mock
    private ITelegramService telegramService;

    @Mock
    private IRabbitMQService rabbitMQService;

    private ConsumerService consumerService;

    @BeforeEach
    void setUp() {
        consumerService = new ConsumerService(mapper, telegramService, rabbitMQService);
    }

    @Test
    void consume_sendsPhotoLocationAndButtons() {
        LandscapeEvent event = new LandscapeEvent(
                "landscape-1",
                "user-1",
                "test@example.com",
                "Test Title",
                "Test Description",
                -12.0464,
                -77.0428,
                "https://example.com/image.jpg"
        );

        TelegramMessage message = new TelegramMessage(
                "landscape-1",
                "https://example.com/image.jpg",
                "Test Title",
                "Test Description",
                -12.0464,
                -77.0428
        );

        when(mapper.toTelegramMessage(event)).thenReturn(message);
        when(telegramService.sendPhoto(any())).thenReturn(Mono.empty());
        when(telegramService.sendLocation(any())).thenReturn(Mono.empty());
        when(telegramService.sendApprovalButtons(any())).thenReturn(Mono.empty());

        consumerService.consume(event);

        verify(mapper).toTelegramMessage(event);
        verify(telegramService).sendPhoto(message);
        verify(telegramService).sendLocation(message);
        verify(telegramService).sendApprovalButtons(message);
    }

    @Test
    void processCallback_withApproveAction_returnsApprovedResponse() {
        when(rabbitMQService.publishStatusEvent(any())).thenReturn(Mono.empty());

        Mono<ApiResponse> result = consumerService.processCallback("APPROVE:landscape-123");

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(200, response.getCode());
                    assertEquals("landscape-123", response.getData());
                    assertNotNull(response.getDateTime());
                })
                .verifyComplete();

        ArgumentCaptor<LandscapeStatusEvent> captor = ArgumentCaptor.forClass(LandscapeStatusEvent.class);
        verify(rabbitMQService).publishStatusEvent(captor.capture());
        assertEquals("APPROVED", captor.getValue().status());
        assertEquals("landscape-123", captor.getValue().landscapeId());
    }

    @Test
    void processCallback_withRejectAction_returnsRejectedResponse() {
        when(rabbitMQService.publishStatusEvent(any())).thenReturn(Mono.empty());

        Mono<ApiResponse> result = consumerService.processCallback("REJECT:landscape-456");

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(200, response.getCode());
                    assertEquals("landscape-456", response.getData());
                    assertNotNull(response.getDateTime());
                })
                .verifyComplete();

        ArgumentCaptor<LandscapeStatusEvent> captor = ArgumentCaptor.forClass(LandscapeStatusEvent.class);
        verify(rabbitMQService).publishStatusEvent(captor.capture());
        assertEquals("REJECTED", captor.getValue().status());
        assertEquals("landscape-456", captor.getValue().landscapeId());
    }
}
