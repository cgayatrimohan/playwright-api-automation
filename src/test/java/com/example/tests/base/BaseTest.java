package com.example.tests.base;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for API tests. Handles Playwright lifecycle and provides a
 * shared {@link APIRequestContext} for making HTTP requests.
 */
public abstract class BaseTest {

    protected static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:3000");

    private static Playwright playwright;
    protected APIRequestContext request;

    @BeforeAll
    static void launchPlaywright() {
        playwright = Playwright.create();
    }

    @AfterAll
    static void closePlaywright() {
        if (playwright != null) {
            playwright.close();
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
