package com.eventisma.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String ticketHash;

    private String eventId;
    private String eventTitle;
    private String eventDate;
    private String eventTime;
    private String venue;
    private String passType;
    private String studentName;
    private String studentId;
    private String department;
    private String email;
    private String phone;
    private double pricePaid;
    private String paymentMethod;
    private LocalDateTime purchasedAt;
    private String seatZone;
    private String qrData;

    public Ticket() {
        this.purchasedAt = LocalDateTime.now();
    }

    // Business method: generate the security hash + QR payload
    public void generateHash() {
        this.ticketHash = "EVT-" + System.currentTimeMillis() + "-" + Math.abs(studentId.hashCode());
        this.qrData = "VERIFIED_EVENTISMA_PASS:" + this.ticketHash + ":" + this.studentId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicketHash() { return ticketHash; }
    public void setTicketHash(String ticketHash) { this.ticketHash = ticketHash; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getPassType() { return passType; }
    public void setPassType(String passType) { this.passType = passType; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getPricePaid() { return pricePaid; }
    public void setPricePaid(double pricePaid) { this.pricePaid = pricePaid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }

    public String getSeatZone() { return seatZone; }
    public void setSeatZone(String seatZone) { this.seatZone = seatZone; }

    public String getQrData() { return qrData; }
    public void setQrData(String qrData) { this.qrData = qrData; }
}
