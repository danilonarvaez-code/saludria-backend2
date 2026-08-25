package com.citas.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ColombiaApiService {

    private final String API_URL = "https://api-colombia.com/api/v1/Department";

    public Object obtenerDepartamentosColombia() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Consume la API pública oficial de Colombia
            return restTemplate.getForObject(API_URL, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al consumir la API externa de Colombia: " + e.getMessage());
        }
    }
}