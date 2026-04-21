package com.wms.warehouse.dto;

import java.time.LocalDateTime;

public class ReceivingResponseDTO {
    private String status;
    private String productSku;
    private String productName;
    private String binCode;
    private Integer quantityReceived;
    private Integer newTotalQuantity;
    private String referenceNumber;
    private LocalDateTime timestamp;
    private String message;

    public ReceivingResponseDTO() {}

    // Getters
    public String getStatus() { return status; }
    public String getProductSku() { return productSku; }
    public String getProductName() { return productName; }
    public String getBinCode() { return binCode; }
    public Integer getQuantityReceived() { return quantityReceived; }
    public Integer getNewTotalQuantity() { return newTotalQuantity; }
    public String getReferenceNumber() { return referenceNumber; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMessage() { return message; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setBinCode(String binCode) { this.binCode = binCode; }
    public void setQuantityReceived(Integer quantityReceived) { this.quantityReceived = quantityReceived; }
    public void setNewTotalQuantity(Integer newTotalQuantity) { this.newTotalQuantity = newTotalQuantity; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setMessage(String message) { this.message = message; }
}