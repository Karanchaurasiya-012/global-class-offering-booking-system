package com.class_booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.class_booking.dto.request.OfferingRequest;
import com.class_booking.dto.request.SessionRequest;
import com.class_booking.dto.response.OfferingResponse;
import com.class_booking.dto.response.SessionResponse;
import com.class_booking.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Teacher APIs", description = "Endpoints for teachers to manage offerings and sessions")
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "Create a new offering/batch")
    @PostMapping("/teachers/{teacherId}/offerings")
    public ResponseEntity<OfferingResponse> createOffering(
            @PathVariable Long teacherId,
            @Valid @RequestBody OfferingRequest request) {
        return new ResponseEntity<>(teacherService.createOffering(teacherId, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Add multiple sessions to an offering")
    @PostMapping("/offerings/{offeringId}/sessions")
    public ResponseEntity<List<SessionResponse>> addSessions(
            @PathVariable Long offeringId,
            @Valid @RequestBody List<SessionRequest> requests) {
        return new ResponseEntity<>(teacherService.addSessions(offeringId, requests), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all offerings and sessions for a teacher")
    @GetMapping("/teachers/{teacherId}/offerings")
    public ResponseEntity<List<OfferingResponse>> getTeacherOfferings(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getTeacherOfferings(teacherId));
    }
}
