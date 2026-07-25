package java_backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

public class TicketService {

    public static class Ticket {
        private String ticketId;
        private String eventId;
        private String eventTitle;
        private String userName;
        private String userEmail;
        private int quantity;
        private double totalPrice;
        private String bookingDate;
        private String verificationHash;

        public Ticket(String eventId, String eventTitle, String userName, String userEmail, int quantity, double pricePerTicket) {
            this.ticketId = "TKN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.eventId = eventId;
            this.eventTitle = eventTitle;
            this.userName = userName;
            this.userEmail = userEmail;
            this.quantity = quantity;
            this.totalPrice = pricePerTicket * quantity;
            this.bookingDate = LocalDateTime.now().toString();
            this.verificationHash = generateVerificationHash(ticketId, eventId, userEmail);
        }

        private String generateVerificationHash(String ticketId, String eventId, String email) {
            try {
                String raw = ticketId + ":" + eventId + ":" + email + ":EVENTNEST_SECRET_2026";
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(raw.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString().substring(0, 16).toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                return "VERIFIED-" + Math.abs(ticketId.hashCode());
            }
        }

        public String getTicketId() { return ticketId; }
        public String getEventId() { return eventId; }
        public String getEventTitle() { return eventTitle; }
        public String getUserName() { return userName; }
        public String getUserEmail() { return userEmail; }
        public int getQuantity() { return quantity; }
        public double getTotalPrice() { return totalPrice; }
        public String getBookingDate() { return bookingDate; }
        public String getVerificationHash() { return verificationHash; }

        public String toJson() {
            return String.format(
                "{\"ticketId\":\"%s\",\"eventId\":\"%s\",\"eventTitle\":\"%s\",\"userName\":\"%s\",\"userEmail\":\"%s\",\"quantity\":%d,\"totalPrice\":%.2f,\"bookingDate\":\"%s\",\"verificationHash\":\"%s\"}",
                ticketId, eventId, escape(eventTitle), escape(userName), escape(userEmail), quantity, totalPrice, bookingDate, verificationHash
            );
        }

        private String escape(String s) {
            if (s == null) return "";
            return s.replace("\"", "\\\"");
        }
    }

    public static Ticket generateTicket(String eventId, String eventTitle, String userName, String userEmail, int quantity, double pricePerTicket) {
        return new Ticket(eventId, eventTitle, userName, userEmail, quantity, pricePerTicket);
    }
}
