package com.eventnest.controller;

import com.eventnest.model.Event;
import com.eventnest.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // GET /api/events?status=PUBLISHED&club={clubId}&page=0&size=10
    @GetMapping
    public Page<Event> getEvents(
            @RequestParam(required = false, defaultValue = "PUBLISHED") String status,
            @RequestParam(required = false) String club,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (club != null && !club.isEmpty()) {
            return eventRepository.findByStatusAndClubId(status, club, pageable);
        }
        return eventRepository.findByStatus(status, pageable);
    }

    // GET /api/events/calendar?startDate=2026-08-01&endDate=2026-08-31 (Returns lightweight date array)
    @GetMapping("/calendar")
    public List<LocalDate> getCalendarEvents(
            @RequestParam LocalDate startDate, 
            @RequestParam LocalDate endDate) {
        return eventRepository.findDistinctEventDatesBetween(startDate, endDate);
    }
}