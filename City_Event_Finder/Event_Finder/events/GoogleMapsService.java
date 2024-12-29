package com.coms309.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsService {
    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}";

    @Value("${google.api.key}")
    private String apiKey;


    private RestTemplate restTemplate;

    public String getCoordinates(String address) {
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("address", address);
            uriVariables.put("key", apiKey);

            ResponseEntity<String> response = restTemplate.getForEntity(GEOCODING_API_URL, String.class, uriVariables);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();  // Return the raw JSON response from Google Geocoding API
            } else {
                throw new RuntimeException("Failed to retrieve coordinates: " + response.getStatusCode());
            }
        } catch (Exception e) {
            // Log the error and stack trace
            System.err.println("Error while fetching coordinates: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error occurred while fetching coordinates: " + e.getMessage());
        }
    }
}
