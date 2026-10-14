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
    public ResponseEntity<?> createOrganizer(@RequestBody Organizer organizer) {
        if (organizer == null || organizer.getName() == null || organizer.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Organizer/Club name cannot be blank.");
        }
        String wanted = normalize(organizer.getName());
        boolean exists = organizerRepository.findAll().stream()
                .anyMatch(o -> normalize(o.getName()).equals(wanted));
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Organizer/Club \"" + organizer.getName().trim() + "\" already exists.");
        }
        organizer.setId(null);
        Organizer saved = organizerRepository.save(organizer);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrganizer(@PathVariable String id, @RequestBody Organizer updatedOrganizer) {
        if (updatedOrganizer == null || updatedOrganizer.getName() == null || updatedOrganizer.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Organizer/Club name cannot be blank.");
        }
        return organizerRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    String wanted = normalize(updatedOrganizer.getName());
                    boolean exists = organizerRepository.findAll().stream()
                            .anyMatch(o -> !o.getId().trim().equalsIgnoreCase(id.trim()) && normalize(o.getName()).equals(wanted));
                    if (exists) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Organizer/Club \"" + updatedOrganizer.getName().trim() + "\" already exists.");
                    }
                    existing.setName(updatedOrganizer.getName());
                    Organizer saved = organizerRepository.save(existing);

                    // Sync organizerName on any events hosted by this organizer
                    List<Event> hosted = eventRepository.findAll().stream()
                            .filter(e -> id.trim().equalsIgnoreCase(e.getOrganizerId() != null ? e.getOrganizerId().trim() : ""))
                            .toList();
                    for (Event e : hosted) {
                        e.setOrganizerName(saved.getName());
                        eventRepository.save(e);
                    }

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

    private static String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
