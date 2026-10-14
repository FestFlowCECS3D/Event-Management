package com.eventisma.controller;

import com.eventisma.model.Event;
import com.eventisma.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean workshopOnly) {

        List<Event> events;
        if (workshopOnly != null && workshopOnly) {
            events = eventRepository.findByIsWorkshopTrue();
        } else if (category != null && !category.equals("all")) {
            events = eventRepository.findByCategoryIgnoreCase(category);
        } else {
            events = eventRepository.findAll();
        }
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        Event dup = findDuplicate(event, null);
        if (dup != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Event \"" + dup.getTitle() + "\" by this organizer/club already exists.");
        }
        // Ensure a create always generates a fresh id, even if the client accidentally sent one.
        event.setId(null);
        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody Event updatedEvent) {
        return eventRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    Event dup = findDuplicate(updatedEvent, id);
                    if (dup != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Event \"" + dup.getTitle() + "\" by this organizer/club already exists.");
                    }
                    existing.setTitle(updatedEvent.getTitle());
                    existing.setSubtitle(updatedEvent.getSubtitle());
                    existing.setType(updatedEvent.getType());
                    existing.setCategory(updatedEvent.getCategory());
                    existing.setDescription(updatedEvent.getDescription());
                    existing.setDate(updatedEvent.getDate());
                    existing.setStartTime(updatedEvent.getStartTime());
                    existing.setEndTime(updatedEvent.getEndTime());
                    existing.setDayNumber(updatedEvent.getDayNumber());
                    existing.setVenue(updatedEvent.getVenue());
                    existing.setPrice(updatedEvent.getPrice());
                    existing.setTotalSlots(updatedEvent.getTotalSlots());
                    existing.setRegisteredSlots(updatedEvent.getRegisteredSlots());
                    existing.setWorkshop(updatedEvent.isWorkshop());
                    existing.setFeatured(updatedEvent.isFeatured());
                    existing.setTags(updatedEvent.getTags());
                    existing.setPerks(updatedEvent.getPerks());
                    existing.setStatus(updatedEvent.getStatus());
                    existing.setOrganizerId(updatedEvent.getOrganizerId());
                    existing.setOrganizerName(updatedEvent.getOrganizerName());
                    existing.setFormat(updatedEvent.getFormat());
                    existing.setStreamLink(updatedEvent.getStreamLink());
                    existing.setPlatform(updatedEvent.getPlatform());
                    existing.setImageUrl(updatedEvent.getImageUrl());
                    Event saved = eventRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---- duplicate detection (exact title match + same organizer/club) ----
    private Event findDuplicate(Event incoming, String excludeId) {
        if (incoming == null || incoming.getTitle() == null) return null;
        return eventRepository.findAll().stream()
                .filter(e -> (excludeId == null || !excludeId.trim().equalsIgnoreCase(e.getId() != null ? e.getId().trim() : ""))
                        && sameOrganizer(e, incoming)
                        && similarTitle(e.getTitle(), incoming.getTitle()))
                .findFirst().orElse(null);
    }

    private static boolean sameOrganizer(Event a, Event b) {
        String idA = a.getOrganizerId(), idB = b.getOrganizerId();
        boolean hasIdA = idA != null && !idA.isBlank();
        boolean hasIdB = idB != null && !idB.isBlank();
        if (hasIdA && hasIdB) {
            return idA.trim().equalsIgnoreCase(idB.trim());
        }
        if (hasIdA != hasIdB) {
            return false;
        }
        String nA = norm(a.getOrganizerName()), nB = norm(b.getOrganizerName());
        boolean hasNameA = !nA.isEmpty();
        boolean hasNameB = !nB.isEmpty();
        if (hasNameA && hasNameB) {
            return nA.equals(nB);
        }
        if (hasNameA != hasNameB) {
            return false;
        }
        // Both have no organizerId and no organizerName
        return true;
    }

    private static boolean similarTitle(String a, String b) {
        String x = norm(a), y = norm(b);
        if (x.isEmpty() || y.isEmpty()) return false;
        return x.equals(y);
    }

    // lowercase, strip punctuation, collapse spaces
    private static String norm(String s) {
        return s == null ? "" : s.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }
}
