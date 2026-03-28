package com.brayanpv.app.service.contracts;

import com.brayanpv.app.model.message.LandscapeEvent;

public interface IConsumerService {

    void consume(LandscapeEvent event);
}
