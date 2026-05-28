package com.class_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.class_booking.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByParentId(Long parentId);
    boolean existsByParentIdAndOfferingId(Long parentId, Long offeringId);
}
