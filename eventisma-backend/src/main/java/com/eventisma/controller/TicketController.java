package com.eventisma.controller;

import com.eventisma.model.Ticket;
import com.eventisma.repository.TicketRepository;
import com.eventisma.service.TicketPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketPdfService ticketPdfService;

    @PostMapping("/checkout")
    public ResponseEntity<Ticket> bookPass(@RequestBody Ticket ticketRequest) {
        ticketRequest.generateHash();
        Ticket saved = ticketRepository.save(ticketRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{ticketId}/pdf")
    public ResponseEntity<byte[]> downloadTicketPdf(@PathVariable String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdfBytes = ticketPdfService.generateTicketPdf(ticket);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Eventisma_Pass_" + ticketId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
