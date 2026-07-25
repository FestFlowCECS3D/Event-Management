package java_backend;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventManager {
    private final Map<String, Event> eventMap = new ConcurrentHashMap<>();

    public EventManager() {
        seedInitialEvents();
    }

    private void seedInitialEvents() {
        Event e1 = new Event(
            "evt-101",
            "Neon Pulse Cyberpunk Rave & Synth Expo",
            "Immerse yourself in a high-octane neon night with live synthwave DJs, holographic laser light displays, and futuristic audio-visual performances.",
            "Music",
            "2026-08-15",
            "21:00",
            "Neo City Underground Arena, Sector 7",
            false,
            45.0,
            500,
            342,
            "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=1200&q=80",
            "Aura Synthwave Crew",
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80",
            Arrays.asList("Synthwave", "Cyberpunk", "Live Music", "Lasers"),
            Arrays.asList("21:00 - Doors Open & Neon Face Painting", "22:30 - DJ Kaelen Live Synth Set", "00:00 - Holographic Laser Showdown"),
            true
        );

        Event e2 = new Event(
            "evt-102",
            "Java 21 & AI Microservices Global Summit",
            "Deep dive into Virtual Threads, Vector API, Spring Boot 3, and building resilient GenAI microservices with ultra-low latency Java runtimes.",
            "Tech",
            "2026-08-20",
            "10:00",
            "Virtual Stream (Discord & YouTube Live)",
            true,
            0.0,
            1200,
            890,
            "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80",
            "Java Global Community",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80",
            Arrays.asList("Java", "Spring Boot", "AI", "Microservices"),
            Arrays.asList("10:00 - Keynote: Java 21 Threads in Action", "11:30 - Building GenAI Pipelines in Spring", "14:00 - Live Coding Workshop"),
            true
        );

        Event e3 = new Event(
            "evt-103",
            "Neon League Esports Tournament - Valorant & Tekken",
            "Battle against elite teams in high-stakes esports arenas with cash prizes, live shoutcasting, and retro arcade free-play zones.",
            "Gaming",
            "2026-08-28",
            "14:00",
            "Cyber Arena X, Downtown Metro",
            false,
            25.0,
            300,
            285,
            "https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1200&q=80",
            "Nexus Gaming Org",
            "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80",
            Arrays.asList("Esports", "Valorant", "Gaming", "Tournament"),
            Arrays.asList("14:00 - Qualifier Rounds", "17:00 - Tekken Top 8 Finals", "20:00 - Valorant Grand Championship"),
            false
        );

        Event e4 = new Event(
            "evt-104",
            "Ethical Hacking & AI Security Hackathon",
            "A 24-hour intense cyberpunk hackathon focused on pentesting LLM APIs, secure coding, and building defensive AI security agents.",
            "Hackathon",
            "2026-09-05",
            "09:00",
            "Tech Hub Tower 4, Silicon Avenue",
            false,
            0.0,
            150,
            142,
            "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=1200&q=80",
            "CyberDef League",
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80",
            Arrays.asList("Security", "Hackathon", "Cybersecurity", "Python/Java"),
            Arrays.asList("09:00 - Problem Statements Release", "12:00 - Mentorship Sessions", "09:00 Next Day - Pitching & Demos"),
            true
        );

        Event e5 = new Event(
            "evt-105",
            "Future Design Systems & UX Glow Workshop",
            "Master dark mode design ergonomics, fluid neon UI patterns, typography contrast math, and high performance web micro-interactions.",
            "Workshop",
            "2026-09-12",
            "15:00",
            "Online Masterclass Room",
            true,
            15.0,
            200,
            110,
            "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?auto=format&fit=crop&w=1200&q=80",
            "GlowUI Studio",
            "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&q=80",
            Arrays.asList("Design", "UI/UX", "Tailwind", "CSS Glow"),
            Arrays.asList("15:00 - Color Contrast Math", "16:15 - Building Glow Design Tokens", "17:30 - Live UI Code Review"),
            false
        );

        addEvent(e1);
        addEvent(e2);
        addEvent(e3);
        addEvent(e4);
        addEvent(e5);
    }

    public void addEvent(Event event) {
        if (event.getId() == null || event.getId().trim().isEmpty()) {
            event.setId("evt-" + UUID.randomUUID().toString().substring(0, 8));
        }
        eventMap.put(event.getId(), event);
    }

    public Event getEvent(String id) {
        return eventMap.get(id);
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventMap.values());
    }

    public List<Event> getEventsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("All")) {
            return getAllEvents();
        }
        return eventMap.values().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Event> searchEvents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEvents();
        }
        String lower = query.toLowerCase();
        return eventMap.values().stream()
                .filter(e -> e.getTitle().toLowerCase().contains(lower) ||
                             e.getDescription().toLowerCase().contains(lower) ||
                             e.getCategory().toLowerCase().contains(lower) ||
                             e.getLocation().toLowerCase().contains(lower) ||
                             e.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public boolean bookTicket(String eventId, int ticketQuantity) {
        Event event = eventMap.get(eventId);
        if (event == null) return false;
        
        synchronized (event) {
            if (event.getSeatsRemaining() >= ticketQuantity) {
                event.setBookedCount(event.getBookedCount() + ticketQuantity);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEvent(String id) {
        return eventMap.remove(id) != null;
    }

    public int getTotalEventCount() {
        return eventMap.size();
    }

    public int getTotalAttendeesCount() {
        return eventMap.values().stream().mapToInt(Event::getBookedCount).sum();
    }

    public double getTotalRevenueGenerated() {
        return eventMap.values().stream().mapToDouble(e -> e.getPrice() * e.getBookedCount()).sum();
    }

    public Map<String, Long> getCategoryDistribution() {
        return eventMap.values().stream()
                .collect(Collectors.groupingBy(Event::getCategory, Collectors.counting()));
    }

    public String getAllEventsAsJson() {
        StringBuilder sb = new StringBuilder("[");
        List<Event> list = getAllEvents();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toJson());
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
