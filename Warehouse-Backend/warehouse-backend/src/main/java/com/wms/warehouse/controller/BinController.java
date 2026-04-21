package com.wms.warehouse.controller;

import com.wms.warehouse.entity.StorageBin;
import com.wms.warehouse.repository.StorageBinRepository;
import com.wms.warehouse.repository.AisleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bins")
public class BinController {

    private final StorageBinRepository binRepository;
    private final AisleRepository aisleRepository;

    public BinController(StorageBinRepository binRepository,
                         AisleRepository aisleRepository) {
        this.binRepository = binRepository;
        this.aisleRepository = aisleRepository;
    }

    // GET all bins
    @GetMapping
    public List<StorageBin> getAllBins() {
        return binRepository.findAll();
    }

    // GET bin by ID
    @GetMapping("/{id}")
    public ResponseEntity<StorageBin> getBinById(@PathVariable Long id) {
        return binRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET bin by bin code
    @GetMapping("/code/{binCode}")
    public ResponseEntity<StorageBin> getBinByCode(@PathVariable String binCode) {
        return binRepository.findByBinCode(binCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET available bins (not full) - FIXED method name
    @GetMapping("/available")
    public List<StorageBin> getAvailableBins() {
        return binRepository.findAvailableBins();
    }

    // GET empty bins - FIXED method name
    @GetMapping("/empty")
    public List<StorageBin> getEmptyBins() {
        return binRepository.findEmptyBins();
    }

    // POST create new bin
    @PostMapping
    public ResponseEntity<StorageBin> createBin(@RequestBody StorageBin bin) {
        // Validate aisle exists
        if (bin.getAisle() == null || bin.getAisle().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!aisleRepository.existsById(bin.getAisle().getId())) {
            return ResponseEntity.badRequest().build();
        }

        // Check if bin code already exists
        if (binRepository.existsByBinCode(bin.getBinCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        StorageBin saved = binRepository.save(bin);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT update bin
    @PutMapping("/{id}")
    public ResponseEntity<StorageBin> updateBin(@PathVariable Long id,
                                                @RequestBody StorageBin binDetails) {
        return binRepository.findById(id)
                .map(bin -> {
                    bin.setBinCode(binDetails.getBinCode());
                    bin.setMaxCapacity(binDetails.getMaxCapacity());
                    bin.setCurrentOccupancy(binDetails.getCurrentOccupancy());
                    bin.setShelfLevel(binDetails.getShelfLevel());
                    bin.setMaxWeight(binDetails.getMaxWeight());
                    return ResponseEntity.ok(binRepository.save(bin));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE bin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBin(@PathVariable Long id) {
        if (!binRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        binRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET bins by aisle
    @GetMapping("/aisle/{aisleId}")
    public List<StorageBin> getBinsByAisle(@PathVariable Long aisleId) {
        return binRepository.findByAisleId(aisleId);
    }

    // GET bins by warehouse
    @GetMapping("/warehouse/{warehouseId}")
    public List<StorageBin> getBinsByWarehouse(@PathVariable Long warehouseId) {
        return binRepository.findByWarehouseId(warehouseId);
    }
}