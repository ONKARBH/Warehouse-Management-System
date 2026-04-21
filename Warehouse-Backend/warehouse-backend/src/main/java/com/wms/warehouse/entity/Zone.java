package com.wms.warehouse.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String zoneCode;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Aisle> aisles = new ArrayList<>();

    public Zone() {}

    public Zone(String name, String zoneCode, Warehouse warehouse) {
        this.name = name;
        this.zoneCode = zoneCode;
        this.warehouse = warehouse;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getZoneCode() { return zoneCode; }
    public String getDescription() { return description; }
    public Warehouse getWarehouse() { return warehouse; }
    public List<Aisle> getAisles() { return aisles; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setZoneCode(String zoneCode) { this.zoneCode = zoneCode; }
    public void setDescription(String description) { this.description = description; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public void setAisles(List<Aisle> aisles) { this.aisles = aisles; }

    // Helper methods
    public void addAisle(Aisle aisle) {
        aisles.add(aisle);
        aisle.setZone(this);
    }

    public void removeAisle(Aisle aisle) {
        aisles.remove(aisle);
        aisle.setZone(null);
    }
}