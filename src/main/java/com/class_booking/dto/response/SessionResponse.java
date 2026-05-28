package com.class_booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionResponse {
    private Long sessionId;
    private Long teacherId;
    private String startTime; // ISO-8601 String format for API response
    private String endTime;   // ISO-8601 String format for API response
}
