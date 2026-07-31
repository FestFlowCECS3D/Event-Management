package com.eventisma.controller;

import com.eventisma.model.Attendee;
import com.eventisma.repository.AttendeeRepository;
import com.eventisma.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendees")
@CrossOrigin(origins = "*")
public class AttendeeController {

    @Autowired
    private AttendeeRepository attendeeRepository;

    @Autowired
    private EventRepository eventRepository;

    // Create a new attendee registration
    @PostMapping
    public ResponseEntity<Attendee> createAttendee(@RequestBody Attendee attendee) {
        if (attendee.getEventId() == null || !eventRepository.existsById(attendee.getEventId())) {
            return ResponseEntity.badRequest().build();
        }
        // Ensure a create always generates a fresh id, even if the client accidentally sent one.
        attendee.setId(null);
        Attendee saved = attendeeRepository.save(attendee);
        return ResponseEntity.ok(saved);
    }

    // Get all attendees
    @GetMapping
    public ResponseEntity<List<Attendee>> getAllAttendees() {
        return ResponseEntity.ok(attendeeRepository.findAll());
    }

    // Get a single attendee by id
    @GetMapping("/{id}")
    public ResponseEntity<Attendee> getAttendeeById(@PathVariable String id) {
        return attendeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all attendees registered for a specific event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Attendee>> getAttendeesByEvent(@PathVariable String eventId) {
        if (!eventRepository.existsById(eventId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attendeeRepository.findByEventId(eventId));
    }

    // Update an existing attendee registration
    @PutMapping("/{id}")
    public ResponseEntity<Attendee> updateAttendee(@PathVariable String id, @RequestBody Attendee updatedAttendee) {
        return attendeeRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedAttendee.getName());
                    existing.setClassName(updatedAttendee.getClassName());
                    existing.setPhoneNumber(updatedAttendee.getPhoneNumber());
                    existing.setEmail(updatedAttendee.getEmail());
                    existing.setTransactionId(updatedAttendee.getTransactionId());
                    if (updatedAttendee.getEventId() != null && eventRepository.existsById(updatedAttendee.getEventId())) {
                        existing.setEventId(updatedAttendee.getEventId());
                    }
                    Attendee saved = attendeeRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete an attendee registration
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendee(@PathVariable String id) {
        if (!attendeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        attendeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
