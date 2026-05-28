package com.class_booking.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull(message = "Offering ID is required for booking")
    private Long offeringId;
}
