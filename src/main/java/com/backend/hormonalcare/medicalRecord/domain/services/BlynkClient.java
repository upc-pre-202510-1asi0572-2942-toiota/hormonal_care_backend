package com.backend.hormonalcare.medicalRecord.domain.services;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BlynkClient {

    private static final String BASE_URL = "https://blynk.cloud/external/api/get?token=zDuTwhyWD7FuoT58DQXcYrOI0UYvPr4Q";

    public int fetchGlucoseLevel() {
        String url = BASE_URL + "&v1";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        try {
            return (int) Float.parseFloat(response); // por si viene "145.0"
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse glucose level from Blynk: " + response);
        }
    }
}
