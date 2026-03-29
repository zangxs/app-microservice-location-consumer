package com.brayanpv.app.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramUpdate(
                             @JsonProperty("update_id") Long updateId,
                             @JsonProperty("callback_query") CallbackQuery callbackQuery
) {}