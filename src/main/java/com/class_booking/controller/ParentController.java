package com.class_booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.class_booking.dto.request.BookingRequest;
import com.class_booking.dto.response.OfferingResponse;
import com.class_booking.service.ParentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
@Tag(name = "Parent APIs", description = "Endpoints for parents to view and book offerings")
public class ParentController {

    private final ParentService parentService;

    @Operation(summary = "View all available offerings in parent's local timezone")
    @GetMapping("/{parentId}/offerings")
    public ResponseEntity<List<OfferingResponse>> getAvailableOfferings(@PathVariable Long parentId) {
        return ResponseEntity.ok(parentService.getAvailableOfferings(parentId));
    }

    @Operation(summary = "Book an entire offering")
    @PostMapping("/{parentId}/bookings")
    public ResponseEntity<Map<String, String>> bookOffering(
            @PathVariable Long parentId,
            @Valid @RequestBody BookingRequest request) {
        String message = parentService.bookOffering(parentId, request);
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.CREATED);
    }

    @Operation(summary = "View all booked offerings and sessions for a parent")
    @GetMapping("/{parentId}/bookings")
    public ResponseEntity<List<OfferingResponse>> getParentBookings(@PathVariable Long parentId) {
        return ResponseEntity.ok(parentService.getParentBookings(parentId));
    }
}
