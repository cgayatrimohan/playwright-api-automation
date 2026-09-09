package com.example.tests;

import com.example.tests.base.BaseTest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sample test demonstrating usage of {@link BaseTest}. Runs against the
 * in-process {@code DummyServer} that {@link BaseTest} starts by default.
 */
class SampleApiTest extends BaseTest {

    @Test
    void getPostReturnsExpectedBody() {
        APIResponse response = request.get("/posts/1");

        assertEquals(200, response.status());

        JsonObject body = new Gson().fromJson(response.text(), JsonObject.class);
        assertEquals(1, body.get("id").getAsInt());
    }
}
