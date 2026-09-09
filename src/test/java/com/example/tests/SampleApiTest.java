package com.example.tests;

import com.example.tests.base.BaseTest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.Test;

import java.util.Map;

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

    @Test
    void createPostReturnsCreatedBody() {
        APIResponse response = request.post("/posts", RequestOptions.create()
                .setData(Map.of("title", "hello", "body", "world")));

        assertEquals(201, response.status());

        JsonObject body = new Gson().fromJson(response.text(), JsonObject.class);
        assertEquals(1, body.get("id").getAsInt());
        assertEquals("hello", body.get("title").getAsString());
        assertEquals("world", body.get("body").getAsString());
    }
}
