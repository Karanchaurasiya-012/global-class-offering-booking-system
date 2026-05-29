package com.class_booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.class_booking.dto.request.OfferingRequest;
import com.class_booking.dto.request.SessionRequest;
import com.class_booking.dto.response.OfferingResponse;
import com.class_booking.dto.response.SessionResponse;
import com.class_booking.entity.Course;
import com.class_booking.entity.Offering;
import com.class_booking.entity.Session;
import com.class_booking.entity.Teacher;
import com.class_booking.exception.ResourceNotFoundException;
import com.class_booking.repository.CourseRepository;
import com.class_booking.repository.OfferingRepository;
import com.class_booking.repository.SessionRepository;
import com.class_booking.repository.TeacherRepository;
import com.class_booking.util.TimezoneUtil;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final OfferingRepository offeringRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public OfferingResponse createOffering(Long teacherId, OfferingRequest request) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Offering offering = Offering.builder()
                .teacher(teacher)
                .course(course)
                .title(request.getTitle())
                .build();

        offering = offeringRepository.save(offering);
        return buildOfferingResponse(offering, teacher.getTimezone(), List.of());
    }

    @Transactional
    public List<SessionResponse> addSessions(Long offeringId, List<SessionRequest> requests) {
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Offering not found"));
        Teacher teacher = offering.getTeacher();

        List<Session> sessions = requests.stream().map(req -> {
            // Convert Teacher's local time to UTC for Database
            Instant startUtc = TimezoneUtil.convertLocalToUtc(req.getStartTimeLocal(), teacher.getTimezone());
            Instant endUtc = TimezoneUtil.convertLocalToUtc(req.getEndTimeLocal(), teacher.getTimezone());

            if (endUtc.isBefore(startUtc)) {
                throw new IllegalArgumentException("End time cannot be before start time");
            }

            return Session.builder()
                    .offering(offering)
                    .teacher(teacher)
                    .startTimeUtc(startUtc)
                    .endTimeUtc(endUtc)
                    .build();
        }).collect(Collectors.toList());

        sessions = sessionRepository.saveAll(sessions);

        return sessions.stream()
                .map(s -> buildSessionResponse(s, teacher.getTimezone()))
                .collect(Collectors.toList());
    }

    public List<OfferingResponse> getTeacherOfferings(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        List<Offering> offerings = offeringRepository.findByTeacherId(teacherId);

        return offerings.stream().map(offering -> {
            List<Session> sessions = sessionRepository.findByOfferingId(offering.getId());
            return buildOfferingResponse(offering, teacher.getTimezone(), sessions);
        }).collect(Collectors.toList());
    }

    // Helper method to convert Entity to Response DTO
    private OfferingResponse buildOfferingResponse(Offering offering, String targetTimezone, List<Session> sessions) {
        List<SessionResponse> sessionResponses = sessions.stream()
                .map(s -> buildSessionResponse(s, targetTimezone))
                .collect(Collectors.toList());

        return OfferingResponse.builder()
                .offeringId(offering.getId())
                .courseName(offering.getCourse().getName())
                .teacherName(offering.getTeacher().getName())
                .title(offering.getTitle())
                .sessions(sessionResponses)
                .build();
    }

    // Helper method to convert UTC Session to Target Timezone (Teacher's local)
    private SessionResponse buildSessionResponse(Session session, String targetTimezone) {
        return SessionResponse.builder()
                .sessionId(session.getId())
                .teacherId(session.getTeacher().getId())
                .startTime(TimezoneUtil.convertUtcToLocalString(session.getStartTimeUtc(), targetTimezone))
                .endTime(TimezoneUtil.convertUtcToLocalString(session.getEndTimeUtc(), targetTimezone))
                .build();
    }
}
