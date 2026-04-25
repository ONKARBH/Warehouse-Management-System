package com.wms.warehouse.dto;

import com.wms.warehouse.entity.Order;
import com.wms.warehouse.entity.OrderLine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private String state;
    private List<OrderLineResponseDTO> orderLines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static class OrderLineResponseDTO {
        private String productSku;
        private String productName;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;

        public static OrderLineResponseDTO fromEntity(OrderLine line) {
            OrderLineResponseDTO dto = new OrderLineResponseDTO();
            dto.setProductSku(line.getProduct().getSku());
            dto.setProductName(line.getProduct().getName());
            dto.setQuantity(line.getQuantity());
            dto.setUnitPrice(line.getUnitPrice());
            dto.setTotalPrice(line.getTotalPrice());
            return dto;
        }

        // Getters and Setters
        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }

    public static OrderResponseDTO fromEntity(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setState(order.getState().name());
        dto.setOrderLines(order.getOrderLines().stream()
                .map(OrderLineResponseDTO::fromEntity)
                .collect(Collectors.toList()));
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
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
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public List<OrderLineResponseDTO> getOrderLines() { return orderLines; }
    public void setOrderLines(List<OrderLineResponseDTO> orderLines) { this.orderLines = orderLines; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}