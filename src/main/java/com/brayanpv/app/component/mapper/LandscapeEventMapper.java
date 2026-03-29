package com.brayanpv.app.component.mapper;

import com.brayanpv.app.model.request.TelegramMessage;
import com.brayanspv.library.model.events.LandscapeEvent;
import org.springframework.stereotype.Component;

@Component
public class LandscapeEventMapper {

    public TelegramMessage toTelegramMessage(LandscapeEvent event) {
        return new TelegramMessage(
                event.landscapeId(),
                event.imageUrl(),
                event.title(),
                event.description(),
                event.latitude(),
                event.longitude()
        );
    }
}
