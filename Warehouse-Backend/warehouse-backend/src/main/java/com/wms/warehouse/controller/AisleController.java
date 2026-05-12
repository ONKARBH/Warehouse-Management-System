package com.wms.warehouse.controller;

import com.wms.warehouse.entity.Aisle;
import com.wms.warehouse.repository.AisleRepository;
import com.wms.warehouse.repository.ZoneRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aisles")
public class AisleController {

    private final AisleRepository aisleRepository;
    private final ZoneRepository zoneRepository;

    public AisleController(AisleRepository aisleRepository, ZoneRepository zoneRepository) {
        this.aisleRepository = aisleRepository;
        this.zoneRepository = zoneRepository;
    }

    // GET all aisles - Both Admin and Operator can view
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public List<Aisle> getAllAisles() {
        return aisleRepository.findAll();
    }

    // GET aisle by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Aisle> getAisleById(@PathVariable Long id) {
        return aisleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create aisle - Only Admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aisle> createAisle(@RequestBody Aisle aisle) {
        // Validate zone exists
        if (aisle.getZone() == null || aisle.getZone().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!zoneRepository.existsById(aisle.getZone().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Aisle saved = aisleRepository.save(aisle);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT update aisle
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Aisle> updateAisle(@PathVariable Long id, @RequestBody Aisle aisleDetails) {
        return aisleRepository.findById(id)
                .map(aisle -> {
                    aisle.setAisleNumber(aisleDetails.getAisleNumber());
                    aisle.setDescription(aisleDetails.getDescription());
                    return ResponseEntity.ok(aisleRepository.save(aisle));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE aisle
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAisle(@PathVariable Long id) {
        if (!aisleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        aisleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}