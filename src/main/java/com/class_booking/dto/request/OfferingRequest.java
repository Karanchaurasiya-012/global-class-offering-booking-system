package com.class_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OfferingRequest {
    
    @NotNull(message = "Course ID cannot be null")
    private Long courseId;
    
    @NotBlank(message = "Title cannot be empty")
    private String title;
}
