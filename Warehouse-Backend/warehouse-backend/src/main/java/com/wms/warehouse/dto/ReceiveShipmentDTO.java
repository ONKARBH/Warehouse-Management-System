package com.wms.warehouse.dto;

public class ReceiveShipmentDTO {
    private String productSku;
    private Integer quantity;
    private String targetBinCode;
    private String referenceNumber;
    private String notes;

    public ReceiveShipmentDTO(String productSku, Integer quantity, String referenceNumber) {}

    public String getProductSku() { return productSku; }
    public Integer getQuantity() { return quantity; }
    public String getTargetBinCode() { return targetBinCode; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getNotes() { return notes; }

    public void setProductSku(String productSku) { this.productSku = productSku; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setTargetBinCode(String targetBinCode) { this.targetBinCode = targetBinCode; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setNotes(String notes) { this.notes = notes; }
}