package com.class_booking.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SessionRequest {

    @NotNull(message = "Start time is required")
    private LocalDateTime startTimeLocal;

    @NotNull(message = "End time is required")
    private LocalDateTime endTimeLocal;
}
