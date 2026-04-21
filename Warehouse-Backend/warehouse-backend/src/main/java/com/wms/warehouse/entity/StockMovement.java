package com.wms.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements", indexes = {
        @Index(name = "idx_product_id", columnList = "product_id"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_reference", columnList = "reference_number")
})
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_bin_id")
    private StorageBin storageBin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Column(nullable = false)
    private Integer quantityChange;

    private Integer quantityBefore;
    private Integer quantityAfter;

    private String referenceNumber;

    private String userId;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public enum MovementType {
        RECEIVED, PICKED, MOVED, ADJUSTED, RETURNED, TRANSFERRED, DAMAGED
    }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public StockMovement() {}

    // Getters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public StorageBin getStorageBin() { return storageBin; }
    public Warehouse getWarehouse() { return warehouse; }
    public MovementType getMovementType() { return movementType; }
    public Integer getQuantityChange() { return quantityChange; }
    public Integer getQuantityBefore() { return quantityBefore; }
    public Integer getQuantityAfter() { return quantityAfter; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getUserId() { return userId; }
    public String getNotes() { return notes; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setProduct(Product product) { this.product = product; }
    public void setStorageBin(StorageBin storageBin) { this.storageBin = storageBin; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }
    public void setQuantityBefore(Integer quantityBefore) { this.quantityBefore = quantityBefore; }
    public void setQuantityAfter(Integer quantityAfter) { this.quantityAfter = quantityAfter; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}