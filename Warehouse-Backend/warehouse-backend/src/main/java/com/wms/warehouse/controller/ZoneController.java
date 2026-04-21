package com.wms.warehouse.controller;

import com.wms.warehouse.entity.Zone;
import com.wms.warehouse.repository.ZoneRepository;
import com.wms.warehouse.repository.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneRepository zoneRepository;
    private final WarehouseRepository warehouseRepository;

    public ZoneController(ZoneRepository zoneRepository, WarehouseRepository warehouseRepository) {
        this.zoneRepository = zoneRepository;
        this.warehouseRepository = warehouseRepository;
    }

    // GET all zones
    @GetMapping
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    // GET zone by ID
    @GetMapping("/{id}")
    public ResponseEntity<Zone> getZoneById(@PathVariable Long id) {
        return zoneRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET zone by code
    @GetMapping("/code/{zoneCode}")
    public ResponseEntity<Zone> getZoneByCode(@PathVariable String zoneCode) {
        return zoneRepository.findByZoneCode(zoneCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET zones by warehouse
    @GetMapping("/warehouse/{warehouseId}")
    public List<Zone> getZonesByWarehouse(@PathVariable Long warehouseId) {
        return zoneRepository.findByWarehouseId(warehouseId);
    }

    // POST create new zone
    @PostMapping
    public ResponseEntity<Zone> createZone(@RequestBody Zone zone) {
        // Validate warehouse exists
        if (zone.getWarehouse() == null || zone.getWarehouse().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!warehouseRepository.existsById(zone.getWarehouse().getId())) {
            return ResponseEntity.badRequest().build();
        }

        // Check if zone code already exists
        if (zone.getZoneCode() != null && zoneRepository.existsByZoneCode(zone.getZoneCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Zone saved = zoneRepository.save(zone);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT update zone
    @PutMapping("/{id}")
    public ResponseEntity<Zone> updateZone(@PathVariable Long id, @RequestBody Zone zoneDetails) {
        return zoneRepository.findById(id)
                .map(zone -> {
                    zone.setName(zoneDetails.getName());
                    zone.setZoneCode(zoneDetails.getZoneCode());
                    zone.setDescription(zoneDetails.getDescription());
                    return ResponseEntity.ok(zoneRepository.save(zone));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE zone
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        if (!zoneRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        zoneRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}