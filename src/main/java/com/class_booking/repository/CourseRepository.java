package com.class_booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.class_booking.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}