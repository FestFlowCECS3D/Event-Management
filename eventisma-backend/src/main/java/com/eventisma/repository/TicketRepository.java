package com.eventnest.repository;

import com.eventnest.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    // Aggregate SQL for Admin financial dashboard metrics
    @Query("SELECT SUM(t.amount) FROM Ticket t WHERE t.paymentStatus = 'PAID'")
    Double calculateTotalRevenue();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.paymentStatus = 'PAID'")
    long countPaidTickets();
}