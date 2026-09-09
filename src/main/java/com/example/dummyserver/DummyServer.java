package com.example.dummyserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal in-process HTTP server used as a stand-in backend for API tests,
 * so tests don't depend on a real external service. Built on the JDK's
 * {@link HttpServer} — no extra dependency required.
 *
 * <p>Endpoints:
 * <ul>
 *     <li>{@code GET /health} → {@code 200 {"status":"UP"}}</li>
 *     <li>{@code GET /posts/{id}} → {@code 200} with a sample post body</li>
 *     <li>{@code POST /posts} → {@code 201} echoing the request body with a generated id</li>
 * </ul>
 */
public class DummyServer {

    private final HttpServer server;
    private final ExecutorService executor;

    /** Starts on an OS-assigned free port. Use {@link #getPort()} to find it. */
    public DummyServer() throws IOException {
        this(0);
    }

    public DummyServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "dummy-server");
            thread.setDaemon(true);
            return thread;
        });
        server.createContext("/health", this::handleHealth);
        server.createContext("/posts", this::handlePosts);
        server.setExecutor(executor);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
        executor.shutdown();
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public String getBaseUrl() {
        return "http://localhost:" + getPort();
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        sendJson(exchange, 200, "{\"status\":\"UP\"}");
    }

    private void handlePosts(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equalsIgnoreCase(method)) {
            String[] segments = path.split("/");
            if (segments.length >= 3 && !segments[2].isBlank()) {
                String id = segments[2];
                String json = String.format(
                        "{\"id\":%s,\"title\":\"sample title\",\"body\":\"sample body\"}", id);
                sendJson(exchange, 200, json);
            } else {
                sendJson(exchange, 200, "[]");
            }
            return;
        }

        if ("POST".equalsIgnoreCase(method)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String json = body.isBlank() ? "{\"id\":1}" : body.replaceFirst("\\{", "{\"id\":1,");
            sendJson(exchange, 201, json);
            return;
        }

        sendJson(exchange, 405, "{\"error\":\"method not allowed\"}");
    }

    private void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** Runs the server standalone on port 3000, for manual/local use. */
    public static void main(String[] args) throws IOException {
        DummyServer server = new DummyServer(3000);
        server.start();
        System.out.println("DummyServer running at " + server.getBaseUrl());
    }
}
