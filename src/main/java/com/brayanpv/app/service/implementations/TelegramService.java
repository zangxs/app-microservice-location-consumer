package com.brayanpv.app.service.implementations;

import com.brayanpv.app.model.request.TelegramMessage;
import com.brayanpv.app.service.contracts.ITelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class TelegramService implements ITelegramService {

    @Value("${telegram.chat-id}")
    private String chatId;
    @Value("${telegram.bot-token}")
    private String botToken;

    private final WebClient webClient;

    public TelegramService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.telegram.org")
                .build();
    }

    @Override
    public Mono<Void> sendPhoto(TelegramMessage message) {
        String caption = String.format("%s*\n\n%s", message.title(), message.description());

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("photo", message.imageUrl());
        body.put("caption", caption);
        body.put("parse_mode", "Markdown");

        return webClient.post()
                .uri("/bot" + botToken + "/sendPhoto")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Photo sent to Telegram: {}", message.landscapeId()))
                .doOnError(e -> log.error("Error sending photo: {}", e.getMessage()));
    }

    @Override
    public Mono<Void> sendApprovalButtons(TelegramMessage message) {
        Map<String, Object> approveButton = new HashMap<>();
        approveButton.put("text", "✅ Aprobar");
        approveButton.put("callback_data", "APPROVE:" + message.landscapeId());

        Map<String, Object> rejectButton = new HashMap<>();
        rejectButton.put("text", "❌ Rechazar");
        rejectButton.put("callback_data", "REJECT:" + message.landscapeId());

        Map<String, Object> keyboard = new HashMap<>();
        keyboard.put("inline_keyboard", List.of(List.of(approveButton, rejectButton)));

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", "¿Aprobar este paisaje?");
        body.put("reply_markup", keyboard);

        return webClient.post()
                .uri("/bot" + botToken + "/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Approval buttons sent: {}", message.landscapeId()))
                .doOnError(e -> log.error("Error sending buttons: {}", e.getMessage()));
    }

    @Override
    public Mono<Void> sendLocation(TelegramMessage message) {
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("latitude", message.latitude());
        body.put("longitude", message.longitude());

        return webClient.post()
                .uri("/bot" + botToken + "/sendLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Location sent to Telegram: {}", message.landscapeId()))
                .doOnError(e -> log.error("Error sending location: {}", e.getMessage()));

    }
}
