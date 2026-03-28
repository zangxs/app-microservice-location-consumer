package com.brayanpv.app.model.request;

import lombok.Data;
import software.amazon.awssdk.services.s3.model.S3Error;

public record TelegramMessage(
        String landscapeId,
        String imageUrl,
        String title,
        String description,
        Double latitude,
        Double longitude
) {}
