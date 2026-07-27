package com.eventnest.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_event_date", columnList = "eventDate"),
    @Index(name = "idx_event_club_id", columnList = "clubId"),
    @Index(name = "idx_event_status", columnList = "status"),
    @Index(name = "idx_event_category", columnList = "cat")
})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String title;
    private String cat;
    private String clubId;
    private String imageUrl;
    private String status;
    
    private LocalDate eventDate; // Indexed date field
    
    private int capacity;
    private int registeredSlots;

    @Version
    private Long version;

    @ElementCollection
    @Fetch(FetchMode.JOIN)
    private List<String> tags;

    @ElementCollection
    @Fetch(FetchMode.JOIN)
    private List<String> perks;

    // Getters and Setters
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    // (Retain other standard getters/setters)
}