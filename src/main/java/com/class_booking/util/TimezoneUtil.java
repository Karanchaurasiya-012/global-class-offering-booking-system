package com.class_booking.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimezoneUtil {

    // Teacher ka local time lekar usko UTC mein convert karta hai DB me save karne ke liye
    public static Instant convertLocalToUtc(LocalDateTime localDateTime, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
    }

    // DB ka UTC time lekar API me bhejte waqt user ke timezone me string format me convert karta hai
    public static String convertUtcToLocalString(Instant instant, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
