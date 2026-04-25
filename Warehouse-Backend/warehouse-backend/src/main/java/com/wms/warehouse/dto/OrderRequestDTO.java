package com.wms.warehouse.dto;

import java.util.List;

public class OrderRequestDTO {
    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private List<OrderLineDTO> orderLines;

    public static class OrderLineDTO {
        private String productSku;
        private Integer quantity;

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<OrderLineDTO> getOrderLines() { return orderLines; }
    public void setOrderLines(List<OrderLineDTO> orderLines) { this.orderLines = orderLines; }
}