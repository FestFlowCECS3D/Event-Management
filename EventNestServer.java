package java_backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class EventNestServer {
    private static final int PORT = 8080;
    private static final EventManager eventManager = new EventManager();
    private static final long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

            // CORS Middleware Header Helper
            server.createContext("/api/java/health", new HealthHandler());
            server.createContext("/api/java/events", new EventsHandler());
            server.createContext("/api/java/tickets/book", new TicketBookingHandler());
            server.createContext("/api/java/stats", new StatsHandler());
            server.createContext("/api/java/execute", new ExecuteJavaHandler());

            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();
            System.out.println("⚡ [Java JDK 17] EventNest Java REST Server running on port " + PORT);
        } catch (IOException e) {
            System.err.println("❌ Failed to start EventNest Java Server: " + e.getMessage());
        }
    }

    private static void enableCorsAndSendHeaders(HttpExchange exchange, int statusCode, int responseLength) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseLength);
    }

    private static void handleOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1);
    }

    // GET /api/java/health
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleOptions(exchange);
                return;
            }

            Runtime runtime = Runtime.getRuntime();
            long totalMem = runtime.totalMemory() / (1024 * 1024);
            long freeMem = runtime.freeMemory() / (1024 * 1024);
            long usedMem = totalMem - freeMem;
            long uptimeSeconds = (System.currentTimeMillis() - startTime) / 1000;

            String response = String.format(
                "{\"status\":\"UP\",\"javaVersion\":\"%s\",\"vendor\":\"%s\",\"totalEvents\":%d,\"usedMemoryMb\":%d,\"totalMemoryMb\":%d,\"uptimeSeconds\":%d,\"serverTime\":\"%s\"}",
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                eventManager.getTotalEventCount(),
                usedMem,
                totalMem,
                uptimeSeconds,
                Instant.now().toString()
            );

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            enableCorsAndSendHeaders(exchange, 200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // GET /api/java/events OR POST /api/java/events
    static class EventsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleOptions(exchange);
                return;
            }

            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                String jsonResponse;

                if (query != null && query.contains("search=")) {
                    String searchTerm = query.split("search=")[1].split("&")[0];
                    List<Event> results = eventManager.searchEvents(java.net.URLDecoder.decode(searchTerm, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < results.size(); i++) {
                        sb.append(results.get(i).toJson());
                        if (i < results.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    jsonResponse = sb.toString();
                } else if (query != null && query.contains("category=")) {
                    String category = query.split("category=")[1].split("&")[0];
                    List<Event> results = eventManager.getEventsByCategory(java.net.URLDecoder.decode(category, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < results.size(); i++) {
                        sb.append(results.get(i).toJson());
                        if (i < results.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    jsonResponse = sb.toString();
                } else {
                    jsonResponse = eventManager.getAllEventsAsJson();
                }

                byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
                enableCorsAndSendHeaders(exchange, 200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();

            } else if (method.equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().reduce("", (acc, line) -> acc + line);

                try {
                    // Primitive JSON parse for event creation
                    Event newEvent = parseEventFromJson(body);
                    eventManager.addEvent(newEvent);

                    String response = String.format("{\"success\":true,\"message\":\"Event added to Java EventManager\",\"event\":%s}", newEvent.toJson());
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    enableCorsAndSendHeaders(exchange, 201, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } catch (Exception e) {
                    String errResponse = "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
                    byte[] bytes = errResponse.getBytes(StandardCharsets.UTF_8);
                    enableCorsAndSendHeaders(exchange, 400, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                }
            }
        }

        private Event parseEventFromJson(String json) {
            // Primitive extractor for event fields
            Event e = new Event();
            e.setId("evt-" + java.util.UUID.randomUUID().toString().substring(0, 8));
            e.setTitle(extractField(json, "title", "New Neon Event"));
            e.setDescription(extractField(json, "description", "Event description..."));
            e.setCategory(extractField(json, "category", "Tech"));
            e.setDate(extractField(json, "date", "2026-09-01"));
            e.setTime(extractField(json, "time", "18:00"));
            e.setLocation(extractField(json, "location", "Virtual Hall"));
            e.setVirtual(json.contains("\"isVirtual\":true"));
            e.setPrice(parseDouble(extractField(json, "price", "0")));
            e.setCapacity(parseInt(extractField(json, "capacity", "100")));
            e.setBookedCount(0);
            e.setImageUrl(extractField(json, "imageUrl", "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=1200&q=80"));
            e.setHostName(extractField(json, "hostName", "Event Host"));
            e.setHostAvatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80");
            e.setFeatured(json.contains("\"isFeatured\":true"));
            return e;
        }

        private String extractField(String json, String key, String defaultVal) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start != -1) {
                int end = json.indexOf("\"", start + pattern.length());
                if (end != -1) {
                    return json.substring(start + pattern.length(), end);
                }
            }
            return defaultVal;
        }

        private double parseDouble(String s) {
            try { return Double.parseDouble(s.replaceAll("[^0-9.]", "")); } catch (Exception e) { return 0.0; }
        }

        private int parseInt(String s) {
            try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return 100; }
        }
    }

    // POST /api/java/tickets/book
    static class TicketBookingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleOptions(exchange);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().reduce("", (acc, line) -> acc + line);

                String eventId = extract(body, "eventId");
                String userName = extract(body, "userName");
                String userEmail = extract(body, "userEmail");
                int qty = parseInt(extract(body, "quantity"), 1);

                Event event = eventManager.getEvent(eventId);
                if (event == null) {
                    sendJson(exchange, 404, "{\"success\":false,\"error\":\"Event not found\"}");
                    return;
                }

                boolean success = eventManager.bookTicket(eventId, qty);
                if (success) {
                    TicketService.Ticket ticket = TicketService.generateTicket(eventId, event.getTitle(), userName, userEmail, qty, event.getPrice());
                    String res = String.format("{\"success\":true,\"message\":\"Ticket successfully booked in Java EventManager\",\"ticket\":%s,\"seatsRemaining\":%d}", ticket.toJson(), event.getSeatsRemaining());
                    sendJson(exchange, 200, res);
                } else {
                    sendJson(exchange, 400, "{\"success\":false,\"error\":\"Not enough seats remaining in Java Event memory\"}");
                }
            }
        }

        private String extract(String json, String key) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start != -1) {
                int end = json.indexOf("\"", start + pattern.length());
                if (end != -1) return json.substring(start + pattern.length(), end);
            }
            return "";
        }

        private int parseInt(String s, int def) {
            try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return def; }
        }

        private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            enableCorsAndSendHeaders(exchange, code, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // GET /api/java/stats
    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleOptions(exchange);
                return;
            }

            Map<String, Long> dist = eventManager.getCategoryDistribution();
            StringBuilder categoryJson = new StringBuilder("{");
            int i = 0;
            for (Map.Entry<String, Long> entry : dist.entrySet()) {
                categoryJson.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
                if (++i < dist.size()) categoryJson.append(",");
            }
            categoryJson.append("}");

            String json = String.format(
                "{\"totalEvents\":%d,\"totalAttendees\":%d,\"totalRevenue\":%.2f,\"categoryDistribution\":%s}",
                eventManager.getTotalEventCount(),
                eventManager.getTotalAttendeesCount(),
                eventManager.getTotalRevenueGenerated(),
                categoryJson.toString()
            );

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            enableCorsAndSendHeaders(exchange, 200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    // POST /api/java/execute -> Dynamic Java code query simulator/executor
    static class ExecuteJavaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                handleOptions(exchange);
                return;
            }

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                InputStream is = exchange.getRequestBody();
                String body = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().reduce("", (acc, line) -> acc + line);

                String code = body.contains("\"code\":\"") ? body.split("\"code\":\"")[1].split("\"")[0] : "";
                code = code.replace("\\n", "\n").replace("\\\"", "\"");

                String output = executeQuerySnippet(code);
                String res = String.format("{\"success\":true,\"output\":\"%s\",\"executedAt\":\"%s\"}", escape(output), Instant.now().toString());

                byte[] bytes = res.getBytes(StandardCharsets.UTF_8);
                enableCorsAndSendHeaders(exchange, 200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        }

        private String executeQuerySnippet(String snippet) {
            if (snippet.contains("getEventsByCategory")) {
                return "Java Stream Result:\nFound " + eventManager.getEventsByCategory("Tech").size() + " Tech events in EventManager Map.";
            } else if (snippet.contains("getTotalRevenue")) {
                return String.format("Java System Revenue Output: $%.2f USD across %d events.", eventManager.getTotalRevenueGenerated(), eventManager.getTotalEventCount());
            } else if (snippet.contains("getAllEvents")) {
                return "Java Stream Output: Retrieved " + eventManager.getAllEvents().size() + " total events from ConcurrentHashMap.";
            } else {
                return "Java 17 Execution Completed:\nOutput: EventNest Java Engine state verified OK. Total active events: " + eventManager.getTotalEventCount();
            }
        }

        private String escape(String s) {
            return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        }
    }
}
