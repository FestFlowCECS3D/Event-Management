package com.eventisma.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String subtitle;
    private String type;
    private String category;

    @Column(length = 2000)
    private String description;

    private String date;
    private String startTime;
    private String endTime;
    private int dayNumber;
    private String venue;
    private double price;
    private int totalSlots;
    private int registeredSlots;
    private boolean isWorkshop;
    private boolean featured;

    // Admin console fields (previously only kept client-side; now persisted)
    private String status;        // DRAFT, PUBLISHED, COMPLETED

    private String organizerId;   // Organizer id (references Organizer entity, persisted in DB)
    private String organizerName; // Snapshot of the organizer/club name at save time, for display without a join

    private String format;        // virtual, inperson, both
    private String streamLink;    // Used when format is virtual/both

    @Lob
    @Column(name = "image_url")
    private String imageUrl;      // Banner image (URL or base64 data URI)

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> perks;

    public Event() {
    }

    public Event(String title, String subtitle, String category, double price, int totalSlots, String venue) {
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.price = price;
        this.totalSlots = totalSlots;
        this.venue = venue;
        this.registeredSlots = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getDayNumber() { return dayNumber; }
    public void setDayNumber(int dayNumber) { this.dayNumber = dayNumber; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public int getRegisteredSlots() { return registeredSlots; }
    public void setRegisteredSlots(int registeredSlots) { this.registeredSlots = registeredSlots; }

    public boolean isWorkshop() { return isWorkshop; }
    public void setWorkshop(boolean isWorkshop) { this.isWorkshop = isWorkshop; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getPerks() { return perks; }
    public void setPerks(List<String> perks) { this.perks = perks; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getStreamLink() { return streamLink; }
    public void setStreamLink(String streamLink) { this.streamLink = streamLink; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
