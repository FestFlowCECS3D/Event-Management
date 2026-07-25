package com.eventisma.service;

import com.eventisma.model.Ticket;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class TicketPdfService {

    public byte[] generateTicketPdf(Ticket ticket) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.CYAN);
            Paragraph title = new Paragraph("EVENTISMA // DIGITAL TICKET PASS", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell("Ticket Security Hash:");
            table.addCell(ticket.getTicketHash());
            table.addCell("Event:");
            table.addCell(ticket.getEventTitle());
            table.addCell("Student Name:");
            table.addCell(ticket.getStudentName());
            table.addCell("Access Tier:");
            table.addCell(ticket.getPassType());
            table.addCell("Venue:");
            table.addCell(ticket.getVenue());
            table.addCell("Status:");
            table.addCell("CONFIRMED & VERIFIED");
            document.add(table);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
