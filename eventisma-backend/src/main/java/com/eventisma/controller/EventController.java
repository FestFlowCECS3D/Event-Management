package com.eventisma.controller;

import com.eventisma.model.Event;
import com.eventisma.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Ensure a create always generates a fresh id, even if the client accidentally sent one.
        event.setId(null);
        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody Event updatedEvent) {
        return eventRepository.findById(id)
                .map(existing -> {
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
}
