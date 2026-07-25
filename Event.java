package java_backend;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Event {
    private String id;
    private String title;
    private String description;
    private String category; // Tech, Music, Gaming, Business, Cyberpunk, Workshop, Party, Hackathon
    private String date; // YYYY-MM-DD
    private String time; // HH:MM
    private String location; // Address or Virtual Link
    private boolean isVirtual;
    private double price; // 0.0 for free
    private int capacity;
    private int bookedCount;
    private String imageUrl;
    private String hostName;
    private String hostAvatar;
    private List<String> tags;
    private List<String> agenda;
    private boolean isFeatured;
    private String createdAt;

    public Event() {
        this.tags = new ArrayList<>();
        this.agenda = new ArrayList<>();
    }

    public Event(String id, String title, String description, String category, String date, String time, 
                 String location, boolean isVirtual, double price, int capacity, int bookedCount, 
                 String imageUrl, String hostName, String hostAvatar, List<String> tags, List<String> agenda, boolean isFeatured) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.isVirtual = isVirtual;
        this.price = price;
        this.capacity = capacity;
        this.bookedCount = bookedCount;
        this.imageUrl = imageUrl;
        this.hostName = hostName;
        this.hostAvatar = hostAvatar;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.agenda = agenda != null ? agenda : new ArrayList<>();
        this.isFeatured = isFeatured;
        this.createdAt = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isVirtual() { return isVirtual; }
    public void setVirtual(boolean virtual) { isVirtual = virtual; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getHostAvatar() { return hostAvatar; }
    public void setHostAvatar(String hostAvatar) { this.hostAvatar = hostAvatar; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getAgenda() { return agenda; }
    public void setAgenda(List<String> agenda) { this.agenda = agenda; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isSoldOut() {
        return bookedCount >= capacity;
    }

    public int getSeatsRemaining() {
        return Math.max(0, capacity - bookedCount);
    }

    // Helper to generate JSON string manually (zero dependency)
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(escapeJson(id)).append("\",");
        json.append("\"title\":\"").append(escapeJson(title)).append("\",");
        json.append("\"description\":\"").append(escapeJson(description)).append("\",");
        json.append("\"category\":\"").append(escapeJson(category)).append("\",");
        json.append("\"date\":\"").append(escapeJson(date)).append("\",");
        json.append("\"time\":\"").append(escapeJson(time)).append("\",");
        json.append("\"location\":\"").append(escapeJson(location)).append("\",");
        json.append("\"isVirtual\":").append(isVirtual).append(",");
        json.append("\"price\":").append(price).append(",");
        json.append("\"capacity\":").append(capacity).append(",");
        json.append("\"bookedCount\":").append(bookedCount).append(",");
        json.append("\"imageUrl\":\"").append(escapeJson(imageUrl)).append("\",");
        json.append("\"hostName\":\"").append(escapeJson(hostName)).append("\",");
        json.append("\"hostAvatar\":\"").append(escapeJson(hostAvatar)).append("\",");
        json.append("\"isFeatured\":").append(isFeatured).append(",");
        
        // Tags array
        json.append("\"tags\":[");
        for (int i = 0; i < tags.size(); i++) {
            json.append("\"").append(escapeJson(tags.get(i))).append("\"");
            if (i < tags.size() - 1) json.append(",");
        }
        json.append("],");

        // Agenda array
        json.append("\"agenda\":[");
        for (int i = 0; i < agenda.size(); i++) {
            json.append("\"").append(escapeJson(agenda.get(i))).append("\"");
            if (i < agenda.size() - 1) json.append(",");
        }
        json.append("]");

        json.append("}");
        return json.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
