package com.wms.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    private String address;
    private String city;
    private String country;


    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Zone> zones = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryItem> inventoryItems = new ArrayList<>();

    public Warehouse() {}

    public Warehouse(String name, String code) {
        this.name = name;
        this.code = code;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public List<Zone> getZones() { return zones; }
    public List<InventoryItem> getInventoryItems() { return inventoryItems; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
    public void setZones(List<Zone> zones) { this.zones = zones; }
    public void setInventoryItems(List<InventoryItem> inventoryItems) { this.inventoryItems = inventoryItems; }

    // Helper methods
    public void addZone(Zone zone) {
        zones.add(zone);
        zone.setWarehouse(this);
    }

    public void removeZone(Zone zone) {
        zones.remove(zone);
        zone.setWarehouse(null);
    }

    public void addInventoryItem(InventoryItem item) {
        inventoryItems.add(item);
        item.setWarehouse(this);
    }

    // Fixed formula with null check
    public Double getTotalInventoryValue() {
        if (inventoryItems == null || inventoryItems.isEmpty()) {
            return 0.0;
        }
        return inventoryItems.stream()
                .filter(item -> item != null && item.getProduct() != null && item.getProduct().getPrice() != null)
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
    }
}