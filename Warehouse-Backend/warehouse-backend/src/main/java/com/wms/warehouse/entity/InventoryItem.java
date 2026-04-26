package com.wms.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "inventory_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "storage_bin_id"})
        })
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "storage_bin_id", nullable = false)
    private StorageBin storageBin;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Version
    private Integer version;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    public InventoryItem() {}

    public InventoryItem(Product product, StorageBin storageBin, Warehouse warehouse, Integer quantity) {
        this.product = product;
        this.storageBin = storageBin;
        this.warehouse = warehouse;
        this.quantity = quantity;
    }

    // Getters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public StorageBin getStorageBin() { return storageBin; }
    public Warehouse getWarehouse() { return warehouse; }
    public Integer getQuantity() { return quantity; }
    public Integer getVersion() { return version; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setProduct(Product product) { this.product = product; }
    public void setStorageBin(StorageBin storageBin) { this.storageBin = storageBin; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setVersion(Integer version) { this.version = version; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public void addQuantity(Integer amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public boolean subtractQuantity(Integer amount) {
        if (amount > 0 && this.quantity >= amount) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }

    public boolean hasEnoughStock(Integer requested) {
        return this.quantity >= requested;
    }
}