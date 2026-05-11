package com.wms.warehouse.controller;

import com.wms.warehouse.entity.StorageBin;
import com.wms.warehouse.repository.StorageBinRepository;
import com.wms.warehouse.repository.AisleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bins")
public class BinController {

    private final StorageBinRepository binRepository;
    private final AisleRepository aisleRepository;

    public BinController(StorageBinRepository binRepository, AisleRepository aisleRepository) {
        this.binRepository = binRepository;
        this.aisleRepository = aisleRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public List<StorageBin> getAllBins() {
        return binRepository.findAll();
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public List<StorageBin> getAvailableBins() {
        return binRepository.findAvailableBins();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBin(@RequestBody StorageBin bin) {
        try {
            if (bin.getAisle() == null || bin.getAisle().getId() == null) {
                return ResponseEntity.badRequest().body("Aisle is required");
            }

            if (!aisleRepository.existsById(bin.getAisle().getId())) {
                return ResponseEntity.badRequest().body("Aisle not found");
            }

            if (binRepository.existsByBinCode(bin.getBinCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Bin code already exists");
            }

            // Fetch the actual aisle
            bin.setAisle(aisleRepository.findById(bin.getAisle().getId()).get());

            if (bin.getCurrentOccupancy() == null) bin.setCurrentOccupancy(0);
            if (bin.getMaxCapacity() == null) bin.setMaxCapacity(100);
            if (bin.getShelfLevel() == null) bin.setShelfLevel(1);

            StorageBin saved = binRepository.save(bin);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBin(@PathVariable Long id) {
        if (!binRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        binRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}