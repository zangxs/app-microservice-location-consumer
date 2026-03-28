package com.brayanpv.app.model.request;

public record TelegramMessage(
        String landscapeId,
        String imageUrl,
        String title,
        String description,
        Double latitude,
        Double longitude
) {}
