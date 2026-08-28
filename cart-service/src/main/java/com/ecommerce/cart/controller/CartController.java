package com.ecommerce.cart.controller;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    public CartController(CartItemRepository cartItemRepository, ProductClient productClient) {
        this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
    }

    // Returns cart items enriched with live product info (name, price, image)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        List<Map<String, Object>> enriched = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            Map<String, Object> product = productClient.getProduct(item.getProductId());
            Map<String, Object> row = new HashMap<>();
            row.put("cartItemId", item.getId());
            row.put("productId", item.getProductId());
            row.put("quantity", item.getQuantity());
            if (product != null) {
                row.put("name", product.get("name"));
                row.put("price", product.get("price"));
                row.put("imageUrl", product.get("imageUrl"));
                BigDecimal price = new BigDecimal(product.get("price").toString());
                total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            enriched.add(row);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", enriched);
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = body.containsKey("quantity") ? Integer.valueOf(body.get("quantity").toString()) : 1;

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUserId(userId);
                    newItem.setProductId(productId);
                    newItem.setQuantity(0);
                    return newItem;
                });
        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{userId}/item/{cartItemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long userId, @PathVariable Long cartItemId, @RequestBody Map<String, Object> body) {
        return cartItemRepository.findById(cartItemId).<ResponseEntity<?>>map(item -> {
            item.setQuantity(Integer.valueOf(body.get("quantity").toString()));
            return ResponseEntity.ok(cartItemRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}/item/{cartItemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartItemRepository.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
