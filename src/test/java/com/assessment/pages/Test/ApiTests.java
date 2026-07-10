package com.assessment.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ApiTests {
    private HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "https://api.restful-api.dev/objects";
    private String generatedId;

    @BeforeClass
    public void setUp() {
        client = HttpClient.newHttpClient();
    }

    @Test(priority = 1)
    public void testCreateObject() throws IOException, InterruptedException {
        String jsonBody = "{\"name\": \"QA Automation Phone\", \"data\": { \"year\": 2026, \"price\": 999.99 } }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);

        Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
        generatedId = (String) responseMap.get("id");
        Assert.assertNotNull(generatedId, "The API response failed to return a generated unique record ID.");
    }

    @Test(priority = 2, dependsOnMethods = {"testCreateObject"})
    public void testReadObject() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + generatedId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);

        Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
        Assert.assertEquals(responseMap.get("name"), "QA Automation Phone");
    }

    @Test(priority = 3, dependsOnMethods = {"testReadObject"})
    public void testUpdateObject() throws IOException, InterruptedException {
        String updatedBody = "{\"name\": \"QA Automation Phone V2\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + generatedId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);

        Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
        Assert.assertEquals(responseMap.get("name"), "QA Automation Phone V2");
    }

    @Test(priority = 4, dependsOnMethods = {"testUpdateObject"})
    public void testDeleteObject() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + generatedId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(priority = 5, dependsOnMethods = {"testDeleteObject"})
    public void testNegativeCaseGetDeletedObject() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + generatedId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(response.statusCode(), 404, "The system did not return a 404 Not Found error for a deleted record ID.");
    }
}
