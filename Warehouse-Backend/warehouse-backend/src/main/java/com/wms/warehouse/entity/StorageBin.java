package com.wms.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage_bins")
public class StorageBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String binCode;

    private Integer maxCapacity = 0;
    private Integer currentOccupancy = 0;
    private Integer shelfLevel = 1;
    private Integer maxWeight = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aisle_id")
    private Aisle aisle;

    @JsonIgnore
    @OneToMany(mappedBy = "storageBin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    public StorageBin() {}

    public StorageBin(String binCode, Integer maxCapacity, Aisle aisle) {
        this.binCode = binCode;
        this.maxCapacity = maxCapacity;
        this.currentOccupancy = 0;
        this.aisle = aisle;
    }

    // Getters
    public Long getId() { return id; }
    public String getBinCode() { return binCode; }
    public Integer getMaxCapacity() { return maxCapacity; }
    public Integer getCurrentOccupancy() { return currentOccupancy; }
    public Integer getShelfLevel() { return shelfLevel; }
    public Integer getMaxWeight() { return maxWeight; }
    public Aisle getAisle() { return aisle; }
    public List<InventoryItem> getInventoryItems() { return inventoryItems; }

    // ADD THIS METHOD - FIXES THE ERROR
    public Warehouse getWarehouse() {
        if (aisle != null && aisle.getZone() != null) {
            return aisle.getZone().getWarehouse();
        }
        return null;
    }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setBinCode(String binCode) { this.binCode = binCode; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setCurrentOccupancy(Integer currentOccupancy) { this.currentOccupancy = currentOccupancy; }
    public void setShelfLevel(Integer shelfLevel) { this.shelfLevel = shelfLevel; }
    public void setMaxWeight(Integer maxWeight) { this.maxWeight = maxWeight; }
    public void setAisle(Aisle aisle) { this.aisle = aisle; }
    public void setInventoryItems(List<InventoryItem> inventoryItems) { this.inventoryItems = inventoryItems; }
}