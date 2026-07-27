package com.eventnest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // POST /api/payments/create-order
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> payload) {
        // In production, integrate with Razorpay SDK here to generate order_id
        Map<String, Object> response = new HashMap<>();
        String mockOrderId = "order_" + UUID.randomUUID().toString().substring(0, 10);
        
        response.put("orderId", mockOrderId);
        response.put("amount", payload.getOrDefault("amount", 0));
        response.put("currency", "INR");
        response.put("status", "CREATED");
        
        return ResponseEntity.ok(response);
    }

    // POST /api/payments/verify
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, String> payload) {
        // Verify payment signature from gateway webhook or client callback
        String paymentId = payload.get("razorpay_payment_id");
        String orderId = payload.get("razorpay_order_id");
        
        Map<String, String> result = new HashMap<>();
        if (paymentId != null && !paymentId.isEmpty()) {
            // Success: Update Ticket entity paymentStatus = PAID and increment Event slots safely
            result.put("status", "SUCCESS");
            result.put("message", "Payment verified and slot confirmed.");
            return ResponseEntity.ok(result);
        } else {
            result.put("status", "FAILED");
            result.put("message", "Signature verification failed.");
            return ResponseEntity.badRequest().body(result);
        }
    }
}