package com.wms.warehouse.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aisles")
public class Aisle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aisleNumber;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @OneToMany(mappedBy = "aisle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StorageBin> bins = new ArrayList<>();

    public Aisle() {}

    public Aisle(String aisleNumber, Zone zone) {
        this.aisleNumber = aisleNumber;
        this.zone = zone;
    }

    // Getters
    public Long getId() { return id; }
    public String getAisleNumber() { return aisleNumber; }
    public String getDescription() { return description; }
    public Zone getZone() { return zone; }
    public List<StorageBin> getBins() { return bins; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAisleNumber(String aisleNumber) { this.aisleNumber = aisleNumber; }
    public void setDescription(String description) { this.description = description; }
    public void setZone(Zone zone) { this.zone = zone; }
    public void setBins(List<StorageBin> bins) { this.bins = bins; }

    // Helper methods
    public void addBin(StorageBin bin) {
        bins.add(bin);
        bin.setAisle(this);
    }

    public void removeBin(StorageBin bin) {
        bins.remove(bin);
        bin.setAisle(null);
    }
}