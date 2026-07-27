package com.eventnest.controller;

import com.eventnest.repository.EventRepository;
import com.eventnest.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Public Stats: Registered users & active event count
    @GetMapping("/stats/summary")
    public Map<String, Object> getPublicSummary() {
        Map<String, Object> stats = new HashMap<>();
        long activeEvents = eventRepository.findAll().stream()
                .filter(e -> "PUBLISHED".equals(e.getStatus()))
                .count();
        long totalRegistrations = ticketRepository.count();

        stats.put("activeEventsCount", activeEvents);
        stats.put("totalRegisteredUsers", totalRegistrations);
        return stats;
    }

    // Admin-Only Stats: Revenue & ticket volume (Protected by Security configuration)
    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminStats() {
        Map<String, Object> adminStats = new HashMap<>();
        long totalTickets = ticketRepository.count();
        
        // Mock revenue calculation based on paid tickets
        double totalRevenue = totalTickets * 15.00; 

        adminStats.put("totalRevenue", totalRevenue);
        adminStats.put("ticketVolume", totalTickets);
        adminStats.put("currency", "INR"); // Tailored for India-based college fests (Razorpay standard)
        return adminStats;
    }
}