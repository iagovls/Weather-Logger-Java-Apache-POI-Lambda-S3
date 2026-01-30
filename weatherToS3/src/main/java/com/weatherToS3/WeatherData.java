package com.weatherToS3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.lambda.powertools.logging.Logging;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherData {

    @Logging
    public Double getActualTemp() throws Exception {
        final HttpClient client = HttpClient.newHttpClient();
        final ObjectMapper mapper = new ObjectMapper();
        Logger logger = LoggerFactory.getLogger(WeatherData.class);

        String BASE_URL = System.getenv("base_url");
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            throw new IllegalArgumentException("base_url environment variable is not set");
        }
        String lat = System.getenv("lat");
        if (lat == null || lat.isEmpty()) {
            throw new IllegalArgumentException("lat environment variable is not set");
        }
        String lon = System.getenv("lon");
        if (lon == null || lon.isEmpty()) {
            throw new IllegalArgumentException("lon environment variable is not set");
        }
        String units = System.getenv("units");
        if (units == null || units.isEmpty()) {
            throw new IllegalArgumentException("units environment variable is not set");
        }
        String api_key = System.getenv("api_key");
        if (api_key == null || api_key.isEmpty()) {
            throw new IllegalArgumentException("api_key environment variable is not set");
        }
        String url = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + api_key + "&units=" + units;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode treeNode = mapper.readTree(response.body());
                return treeNode.path("main").get("temp").asDouble();
            } else {
                logger.info("Error on request OpenWeather. StatusCode: " + response.statusCode());
                throw new Exception("Erro");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

