package com.class_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.class_booking.dto.request.BookingRequest;
import com.class_booking.dto.response.OfferingResponse;
import com.class_booking.dto.response.SessionResponse;
import com.class_booking.entity.Booking;
import com.class_booking.entity.Offering;
import com.class_booking.entity.Parent;
import com.class_booking.entity.Session;
import com.class_booking.exception.BookingConflictException;
import com.class_booking.exception.ResourceNotFoundException;
import com.class_booking.repository.BookingRepository;
import com.class_booking.repository.OfferingRepository;
import com.class_booking.repository.ParentRepository;
import com.class_booking.repository.SessionRepository;
import com.class_booking.util.TimezoneUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final OfferingRepository offeringRepository;
    private final SessionRepository sessionRepository;
    private final BookingRepository bookingRepository;

    public List<OfferingResponse> getAvailableOfferings(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        List<Offering> offerings = offeringRepository.findAll();

        return offerings.stream().map(offering -> {
            List<Session> sessions = sessionRepository.findByOfferingId(offering.getId());
            return buildOfferingResponse(offering, parent.getTimezone(), sessions);
        }).collect(Collectors.toList());
    }

    // IMPORTANT: @Transactional ensures that the PESSIMISTIC_WRITE lock works properly
    @Transactional
    public String bookOffering(Long parentId, BookingRequest request) {
        // 1. Lock the Parent row to prevent concurrent bookings
        Parent parent = parentRepository.findByIdWithLock(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        Offering newOffering = offeringRepository.findById(request.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));

        // 2. Prevent duplicate booking for the same offering
        if (bookingRepository.existsByParentIdAndOfferingId(parentId, newOffering.getId())) {
            throw new BookingConflictException("You have already booked this offering.");
        }

        // 3. Fetch sessions of the new offering
        List<Session> newSessions = sessionRepository.findByOfferingId(newOffering.getId());
        if (newSessions.isEmpty()) {
            throw new IllegalArgumentException("Cannot book an offering with no sessions.");
        }

        // 4. Fetch all existing sessions the parent has already booked
        List<Booking> existingBookings = bookingRepository.findByParentId(parentId);
        List<Session> existingSessions = new ArrayList<>();
        for (Booking booking : existingBookings) {
            existingSessions.addAll(sessionRepository.findByOfferingId(booking.getOffering().getId()));
        }

        // 5. CONFLICT DETECTION LOGIC (Overlap check)
        for (Session newSession : newSessions) {
            for (Session existingSession : existingSessions) {
                // Formula: StartA < EndB AND StartB < EndA
                boolean isOverlap = newSession.getStartTimeUtc().isBefore(existingSession.getEndTimeUtc()) &&
                                    existingSession.getStartTimeUtc().isBefore(newSession.getEndTimeUtc());
                
                if (isOverlap) {
                    throw new BookingConflictException("Time conflict detected! Your existing booked session from "
                            + TimezoneUtil.convertUtcToLocalString(existingSession.getStartTimeUtc(), parent.getTimezone())
                            + " overlaps with the new session.");
                }
            }
        }

        // 6. Save the booking
        Booking booking = Booking.builder()
                .parent(parent)
                .offering(newOffering)
                .build();
        bookingRepository.save(booking);

        return "Booking successful for " + newOffering.getTitle();
    }

    public List<OfferingResponse> getParentBookings(Long parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        List<Booking> bookings = bookingRepository.findByParentId(parentId);

        return bookings.stream().map(booking -> {
            Offering offering = booking.getOffering();
            List<Session> sessions = sessionRepository.findByOfferingId(offering.getId());
            return buildOfferingResponse(offering, parent.getTimezone(), sessions);
        }).collect(Collectors.toList());
    }

    private OfferingResponse buildOfferingResponse(Offering offering, String targetTimezone, List<Session> sessions) {
        List<SessionResponse> sessionResponses = sessions.stream()
                .map(s -> SessionResponse.builder()
                        .sessionId(s.getId())
                        .teacherId(s.getTeacher().getId())
                        // Convert UTC to Parent's Local Timezone
                        .startTime(TimezoneUtil.convertUtcToLocalString(s.getStartTimeUtc(), targetTimezone))
                        .endTime(TimezoneUtil.convertUtcToLocalString(s.getEndTimeUtc(), targetTimezone))
                        .build())
                .collect(Collectors.toList());

        return OfferingResponse.builder()
                .offeringId(offering.getId())
                .courseName(offering.getCourse().getName())
                .teacherName(offering.getTeacher().getName())
                .title(offering.getTitle())
                .sessions(sessionResponses)
                .build();
    }
}
