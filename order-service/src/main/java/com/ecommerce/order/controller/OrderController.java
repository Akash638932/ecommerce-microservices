package com.ecommerce.order.controller;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;

    public OrderController(OrderRepository orderRepository, CartClient cartClient) {
        this.orderRepository = orderRepository;
        this.cartClient = cartClient;
    }

    // Places an order from the user's current cart (fetched live from cart-service)
    @PostMapping("/{userId}/checkout")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> checkout(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Map<String, Object> cart = cartClient.getCart(userId);
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cart.get("items");

        if (cartItems == null || cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cart is empty"));
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(String.valueOf(body.getOrDefault("shippingAddress", "N/A")));

        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> ci : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(Long.valueOf(ci.get("productId").toString()));
            item.setProductName(String.valueOf(ci.get("name")));
            item.setQuantity(Integer.valueOf(ci.get("quantity").toString()));
            BigDecimal price = new BigDecimal(ci.get("price").toString());
            item.setUnitPrice(price);
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            order.getItems().add(item);
        }
        order.setTotalAmount(total);
        order.setStatus("PLACED");

        Order saved = orderRepository.save(order);
        cartClient.clearCart(userId);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{userId}")
    public List<Order> getOrders(@PathVariable Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/detail/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
