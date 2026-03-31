package com.brayanpv.app.component.handler;

import com.brayanpv.app.model.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebInputException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleServerWebInputException_returnsBadRequestWithErrorMessages() {
        String validationMessage = "Validation failed for argument [0]: [Field error in object 'telegramUpdate' on field 'callbackQuery': rejected value [null]; codes [NotNull.telegramUpdate.callbackQuery]; arguments []; default message [must not be null]]";

        ServerWebInputException exception = new ServerWebInputException(validationMessage);

        ResponseEntity<?> response = handler.handleServerWebInputException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals(400, apiResponse.getCode());
        assertNotNull(apiResponse.getDateTime());
        assertNotNull(apiResponse.getData());

        Map<String, Object> data = (Map<String, Object>) apiResponse.getData();
        assertNotNull(data.get("errors"));
    }

    @Test
    void handleServerWebInputException_withMultipleErrors() {
        String validationMessage = "Validation failed: [Field error on field 'a'; default message [error a]] [Field error on field 'b'; default message [error b]]";

        ServerWebInputException exception = new ServerWebInputException(validationMessage);

        ResponseEntity<?> response = handler.handleServerWebInputException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        Map<String, Object> data = (Map<String, Object>) apiResponse.getData();
        assertNotNull(data.get("errors"));
    }

    @Test
    void handleServerWebInputException_withEmptyMessage() {
        ServerWebInputException exception = new ServerWebInputException("");

        ResponseEntity<?> response = handler.handleServerWebInputException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = (ApiResponse) response.getBody();
        assertEquals(400, apiResponse.getCode());
    }
}
