package com.class_booking.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.class_booking.entity.Parent;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    
    // Yeh method Parent ki row ko database level par lock kar dega transaction complete hone tak
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Parent p WHERE p.id = :id")
    Optional<Parent> findByIdWithLock(@Param("id") Long id);
}
