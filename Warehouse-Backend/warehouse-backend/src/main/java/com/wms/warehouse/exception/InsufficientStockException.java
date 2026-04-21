package com.wms.warehouse.exception;

public class InsufficientStockException extends RuntimeException {

    private String productSku;
    private Integer requestedQuantity;
    private Integer availableQuantity;

    public InsufficientStockException(String productSku, Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                productSku, requestedQuantity, availableQuantity));
        this.productSku = productSku;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public InsufficientStockException(String message) {
        super(message);
    }

    // Getters
    public String getProductSku() { return productSku; }
    public Integer getRequestedQuantity() { return requestedQuantity; }
    public Integer getAvailableQuantity() { return availableQuantity; }
}