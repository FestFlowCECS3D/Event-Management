package com.eventisma.controller;

import com.eventisma.model.Event;
import com.eventisma.model.Organizer;
import com.eventisma.repository.EventRepository;
import com.eventisma.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizers")
@CrossOrigin(origins = "*")
public class OrganizerController {

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<Organizer>> getAllOrganizers() {
        return ResponseEntity.ok(organizerRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organizer> getOrganizerById(@PathVariable String id) {
        return organizerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Organizer> createOrganizer(@RequestBody Organizer organizer) {
        organizer.setId(null);
        Organizer saved = organizerRepository.save(organizer);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Organizer> updateOrganizer(@PathVariable String id, @RequestBody Organizer updatedOrganizer) {
        return organizerRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedOrganizer.getName());
                    Organizer saved = organizerRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganizer(@PathVariable String id) {
        if (!organizerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        boolean hostingEvents = eventRepository.findAll().stream()
                .anyMatch(e -> id.equals(e.getOrganizerId()));
        if (hostingEvents) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete organizer: currently hosting active events.");
        }
        organizerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
