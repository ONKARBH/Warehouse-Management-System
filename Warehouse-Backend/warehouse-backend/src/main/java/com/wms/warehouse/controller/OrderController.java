package com.wms.warehouse.controller;

import com.wms.warehouse.dto.OrderRequestDTO;
import com.wms.warehouse.dto.OrderResponseDTO;
import com.wms.warehouse.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrder(orderNumber));
    }

    @GetMapping("/state/{state}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByState(@PathVariable String state) {
        return ResponseEntity.ok(orderService.getOrdersByState(com.wms.warehouse.entity.Order.OrderState.valueOf(state)));
    }

    @PutMapping("/{orderNumber}/state")
    public ResponseEntity<OrderResponseDTO> updateOrderState(@PathVariable String orderNumber,
                                                             @RequestParam String state) {
        OrderResponseDTO response = orderService.transitionOrderState(orderNumber, state);
        return ResponseEntity.ok(response);
    }
}