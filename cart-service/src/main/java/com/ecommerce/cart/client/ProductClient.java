package com.ecommerce.cart.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8082/api/products";

    public ProductClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getProduct(Long productId) {
        try {
            return restTemplate.getForObject(PRODUCT_SERVICE_URL + "/" + productId, Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
