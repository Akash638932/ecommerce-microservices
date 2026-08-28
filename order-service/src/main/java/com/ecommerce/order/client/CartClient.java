package com.ecommerce.order.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CartClient {

    private final RestTemplate restTemplate;
    private static final String CART_SERVICE_URL = "http://localhost:8083/api/cart";

    public CartClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCart(Long userId) {
        return restTemplate.getForObject(CART_SERVICE_URL + "/" + userId, Map.class);
    }

    public void clearCart(Long userId) {
        restTemplate.delete(CART_SERVICE_URL + "/" + userId + "/clear");
    }
}
