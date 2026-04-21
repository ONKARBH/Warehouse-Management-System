package com.wms.warehouse.controller;

import com.wms.warehouse.entity.InventoryItem;
import com.wms.warehouse.repository.InventoryItemRepository;
import com.wms.warehouse.repository.ProductRepository;
import com.wms.warehouse.repository.StorageBinRepository;
import com.wms.warehouse.repository.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryItemController {

    private final InventoryItemRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final StorageBinRepository binRepo;
    private final WarehouseRepository warehouseRepo;

    public InventoryItemController(InventoryItemRepository inventoryRepo,
                                   ProductRepository productRepo,
                                   StorageBinRepository binRepo,
                                   WarehouseRepository warehouseRepo) {
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.binRepo = binRepo;
        this.warehouseRepo = warehouseRepo;
    }

    @GetMapping
    public List<InventoryItem> getAllInventory() {
        return inventoryRepo.findAll();
    }

    @GetMapping("/product/{sku}")
    public List<InventoryItem> getInventoryByProduct(@PathVariable String sku) {
        return inventoryRepo.findByProductSku(sku);
    }

    @GetMapping("/bin/{binCode}")
    public List<InventoryItem> getInventoryByBin(@PathVariable String binCode) {
        return inventoryRepo.findByStorageBin_BinCode(binCode);
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createInventoryItem(@RequestBody InventoryItem item) {
        // Fixed: Check if product exists using product ID
        if (item.getProduct() == null || item.getProduct().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!productRepo.existsById(item.getProduct().getId())) {
            return ResponseEntity.badRequest().body(null);
        }

        // Fixed: Check if bin exists using storage bin ID
        if (item.getStorageBin() == null || item.getStorageBin().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!binRepo.existsById(item.getStorageBin().getId())) {
            return ResponseEntity.badRequest().body(null);
        }

        InventoryItem saved = inventoryRepo.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<InventoryItem> updateQuantity(@PathVariable Long id,
                                                        @RequestParam Integer newQuantity) {
        return inventoryRepo.findById(id)
                .map(item -> {
                    item.setQuantity(newQuantity);
                    return ResponseEntity.ok(inventoryRepo.save(item));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}