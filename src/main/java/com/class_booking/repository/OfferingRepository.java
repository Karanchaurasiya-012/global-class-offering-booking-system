package com.class_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.class_booking.entity.Offering;

import java.util.List;

@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByTeacherId(Long teacherId);
}