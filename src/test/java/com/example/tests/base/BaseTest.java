package com.example.tests.base;

import com.example.dummyserver.DummyServer;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Base class for API tests. Handles Playwright lifecycle and provides a
 * shared {@link APIRequestContext} for making HTTP requests.
 *
 * <p>By default, tests run against an in-process {@link DummyServer} so they
 * don't depend on a real backend. Pass {@code -DbaseUrl=...} to point at a
 * real service instead, in which case no dummy server is started.
 */
public abstract class BaseTest {

    protected static String BASE_URL;

    private static Playwright playwright;
    private static DummyServer dummyServer;
    protected APIRequestContext request;

    @BeforeAll
    static void setUp() {
        playwright = Playwright.create();

        String configuredUrl = System.getProperty("baseUrl");
        if (configuredUrl != null) {
            BASE_URL = configuredUrl;
        } else {
            try {
                dummyServer = new DummyServer();
                dummyServer.start();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to start DummyServer", e);
            }
            BASE_URL = dummyServer.getBaseUrl();
        }
    }

    @AfterAll
    static void tearDown() {
        if (playwright != null) {
            playwright.close();
        }
        if (dummyServer != null) {
            dummyServer.stop();
        }
    }

    @BeforeEach
    void createRequestContext() {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(BASE_URL)
        );
    }

    @AfterEach
    void closeRequestContext() {
        if (request != null) {
            request.dispose();
        }
    }
}
