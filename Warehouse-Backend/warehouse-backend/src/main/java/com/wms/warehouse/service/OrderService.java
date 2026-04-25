package com.wms.warehouse.service;

import com.wms.warehouse.dto.OrderRequestDTO;
import com.wms.warehouse.dto.OrderResponseDTO;
import com.wms.warehouse.entity.*;
import com.wms.warehouse.exception.InsufficientStockException;
import com.wms.warehouse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductRepository productRepository;
    private final InventoryItemRepository inventoryRepo;
    private final StockMovementRepository movementRepo;

    public OrderService(OrderRepository orderRepository,
                        OrderLineRepository orderLineRepository,
                        ProductRepository productRepository,
                        InventoryItemRepository inventoryRepo,
                        StockMovementRepository movementRepo) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
        this.productRepository = productRepository;
        this.inventoryRepo = inventoryRepo;
        this.movementRepo = movementRepo;
    }

    // Create new order
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        // Generate order number
        String orderNumber = "ORD-" + System.currentTimeMillis();

        Order order = new Order(orderNumber, request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(request.getShippingAddress());

        // Add order lines
        for (OrderRequestDTO.OrderLineDTO lineDTO : request.getOrderLines()) {
            Product product = productRepository.findBySku(lineDTO.getProductSku())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + lineDTO.getProductSku()));

            OrderLine line = new OrderLine(product, lineDTO.getQuantity(), product.getPrice());
            order.addOrderLine(line);
        }

        Order saved = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(saved);
    }

    // Get order by number
    public OrderResponseDTO getOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        return OrderResponseDTO.fromEntity(order);
    }

    // Get all orders
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    // Get orders by state
    public List<OrderResponseDTO> getOrdersByState(Order.OrderState state) {
        return orderRepository.findByState(state).stream()
                .map(OrderResponseDTO::fromEntity)
                .toList();
    }

    // Transition order state - CRITICAL: Stock decrement happens at PACKED
    @Transactional
    public OrderResponseDTO transitionOrderState(String orderNumber, String newState) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));

        Order.OrderState targetState = Order.OrderState.valueOf(newState);
        Order.OrderState currentState = order.getState();

        // Handle state transitions
        if (targetState == Order.OrderState.PICKING && currentState == Order.OrderState.PENDING) {
            // Check stock availability before moving to PICKING
            checkStockAvailability(order);
            order.transitionToPicking();

        } else if (targetState == Order.OrderState.PACKED && currentState == Order.OrderState.PICKING) {
            // CRITICAL: Decrement stock when moving to PACKED
            decrementStock(order);
            order.transitionToPacked();

        } else if (targetState == Order.OrderState.SHIPPED && currentState == Order.OrderState.PACKED) {
            order.transitionToShipped();

        } else if (targetState == Order.OrderState.CANCELLED) {
            if (currentState == Order.OrderState.PICKING) {
                // Return stock to inventory if cancelling during picking
                returnStockToInventory(order);
            }
            order.transitionToCancelled();
        } else {
            throw new IllegalStateException("Invalid state transition from " + currentState + " to " + targetState);
        }

        Order updated = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(updated);
    }

    // Check if sufficient stock is available
    private void checkStockAvailability(Order order) {
        for (OrderLine line : order.getOrderLines()) {
            String sku = line.getProduct().getSku();
            Integer requested = line.getQuantity();
            Integer available = inventoryRepo.getTotalStockBySku(sku);

            if (available < requested) {
                throw new InsufficientStockException(sku, requested, available);
            }
        }
    }

    // CRITICAL: Decrement stock when order is PACKED
    private void decrementStock(Order order) {
        for (OrderLine line : order.getOrderLines()) {
            Product product = line.getProduct();
            int remainingToPick = line.getQuantity();

            // Find all bins containing this product (FIFO - oldest first)
            List<InventoryItem> stockLocations = inventoryRepo.findByProductSku(product.getSku());

            for (InventoryItem item : stockLocations) {
                if (remainingToPick <= 0) break;

                int pickQuantity = Math.min(remainingToPick, item.getQuantity());
                int oldQuantity = item.getQuantity();
                int newQuantity = oldQuantity - pickQuantity;

                // Update inventory
                item.setQuantity(newQuantity);
                inventoryRepo.save(item);

                // Update bin occupancy
                StorageBin bin = item.getStorageBin();
                bin.setCurrentOccupancy(bin.getCurrentOccupancy() - pickQuantity);

                // Record stock movement
                StockMovement movement = new StockMovement();
                movement.setProduct(product);
                movement.setStorageBin(bin);
                movement.setWarehouse(bin.getWarehouse());
                movement.setMovementType(StockMovement.MovementType.PICKED);
                movement.setQuantityChange(-pickQuantity);
                movement.setQuantityBefore(oldQuantity);
                movement.setQuantityAfter(newQuantity);
                movement.setReferenceNumber(order.getOrderNumber());
                movement.setUserId("SYSTEM");
                movementRepo.save(movement);

                remainingToPick -= pickQuantity;
            }

            if (remainingToPick > 0) {
                throw new InsufficientStockException(product.getSku(), line.getQuantity(),
                        line.getQuantity() - remainingToPick);
            }
        }
    }

    // Return stock to inventory when order is cancelled during picking
    private void returnStockToInventory(Order order) {
        for (OrderLine line : order.getOrderLines()) {
            // This would need to track which bins were picked from
            // For simplicity, we add back to first available bin
            List<InventoryItem> stockLocations = inventoryRepo.findByProductSku(line.getProduct().getSku());
            if (!stockLocations.isEmpty()) {
                InventoryItem item = stockLocations.get(0);
                int oldQuantity = item.getQuantity();
                item.setQuantity(oldQuantity + line.getQuantity());
                inventoryRepo.save(item);

                // Record movement
                StockMovement movement = new StockMovement();
                movement.setProduct(line.getProduct());
                movement.setMovementType(StockMovement.MovementType.RETURNED);
                movement.setQuantityChange(line.getQuantity());
                movement.setQuantityBefore(oldQuantity);
                movement.setQuantityAfter(oldQuantity + line.getQuantity());
                movement.setReferenceNumber(order.getOrderNumber());
                movement.setUserId("SYSTEM");
                movementRepo.save(movement);
            }
        }
    }
}