package com.eventnest.repository;

import com.eventnest.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    // Paginated and filtered event queries
    Page<Event> findByStatus(String status, Pageable pageable);
    Page<Event> findByStatusAndClubId(String status, String clubId, Pageable pageable);

    // Lightweight Calendar Query: returns only date strings or local dates, avoiding full object overhead
    @Query("SELECT DISTINCT e.eventDate FROM Event e WHERE e.eventDate BETWEEN :startDate AND :endDate")
    List<LocalDate> findDistinctEventDatesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Aggregate count directly in the database
    @Query("SELECT COUNT(e) FROM Event e WHERE e.status = 'PUBLISHED'")
    long countPublishedEvents();
}