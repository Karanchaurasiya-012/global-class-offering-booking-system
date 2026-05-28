package com.class_booking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OfferingResponse {
    private Long offeringId;
    private String courseName;
    private String teacherName;
    private String title;
    private List<SessionResponse> sessions;
}
