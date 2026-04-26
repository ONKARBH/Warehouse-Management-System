package com.wms.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;
    private String category;
    private Double weight;
    private Double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Product() {}

    public Product(String sku, String name) {
        this.sku = sku;
        this.name = name;
    }

    // Getters with proper return types
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Double getWeight() { return weight; }
    public Double getPrice() { return price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<InventoryItem> getInventoryItems() { return inventoryItems; }

    // Setters with proper parameter types
    public void setId(Long id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setPrice(Double price) { this.price = price; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setInventoryItems(List<InventoryItem> inventoryItems) { this.inventoryItems = inventoryItems; }

    // Helper methods
    public void addInventoryItem(InventoryItem item) {
        inventoryItems.add(item);
        item.setProduct(this);
    }

    public void removeInventoryItem(InventoryItem item) {
        inventoryItems.remove(item);
        item.setProduct(null);
    }

    public Integer getTotalQuantity() {
        return inventoryItems.stream()
                .mapToInt(InventoryItem::getQuantity)
                .sum();
    }
}