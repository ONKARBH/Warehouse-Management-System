package com.wms.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    private String customerName;
    private String customerEmail;
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderLine> orderLines = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum OrderState {
        PENDING,    // Order received, stock not yet allocated
        PICKING,    // Stock allocated, pick list generated
        PACKED,     // Items packed, ready for shipping
        SHIPPED,    // Shipped, stock officially deducted
        CANCELLED   // Order cancelled, stock returned
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Order() {}

    public Order(String orderNumber, String customerName) {
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.state = OrderState.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }

    public List<OrderLine> getOrderLines() { return orderLines; }
    public void setOrderLines(List<OrderLine> orderLines) { this.orderLines = orderLines; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public void addOrderLine(OrderLine line) {
        orderLines.add(line);
        line.setOrder(this);
    }

    public void transitionToPicking() {
        if (this.state != OrderState.PENDING) {
            throw new IllegalStateException("Can only transition from PENDING to PICKING");
        }
        this.state = OrderState.PICKING;
    }

    public void transitionToPacked() {
        if (this.state != OrderState.PICKING) {
            throw new IllegalStateException("Can only transition from PICKING to PACKED");
        }
        this.state = OrderState.PACKED;
    }

    public void transitionToShipped() {
        if (this.state != OrderState.PACKED) {
            throw new IllegalStateException("Can only transition from PACKED to SHIPPED");
        }
        this.state = OrderState.SHIPPED;
    }

    public void transitionToCancelled() {
        if (this.state == OrderState.SHIPPED) {
            throw new IllegalStateException("Cannot cancel shipped order");
        }
        this.state = OrderState.CANCELLED;
    }
}