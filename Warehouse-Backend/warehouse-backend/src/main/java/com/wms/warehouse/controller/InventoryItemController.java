package com.wms.warehouse.controller;

import com.wms.warehouse.entity.InventoryItem;
import com.wms.warehouse.entity.Product;
import com.wms.warehouse.entity.StorageBin;
import com.wms.warehouse.entity.Warehouse;
import com.wms.warehouse.repository.InventoryItemRepository;
import com.wms.warehouse.repository.ProductRepository;
import com.wms.warehouse.repository.StorageBinRepository;
import com.wms.warehouse.repository.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> createInventoryItem(@RequestBody Map<String, Object> request) {
        try {
            // Extract IDs from request
            Map<String, Integer> productMap = (Map<String, Integer>) request.get("product");
            Map<String, Integer> binMap = (Map<String, Integer>) request.get("storageBin");
            Map<String, Integer> warehouseMap = (Map<String, Integer>) request.get("warehouse");
            Integer quantity = (Integer) request.get("quantity");

            Long productId = productMap.get("id").longValue();
            Long binId = binMap.get("id").longValue();
            Long warehouseId = warehouseMap.get("id").longValue();

            // Fetch entities
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            StorageBin bin = binRepo.findById(binId)
                    .orElseThrow(() -> new RuntimeException("Bin not found with id: " + binId));
            Warehouse warehouse = warehouseRepo.findById(warehouseId)
                    .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));

            // Create inventory item
            InventoryItem item = new InventoryItem();
            item.setProduct(product);
            item.setStorageBin(bin);
            item.setWarehouse(warehouse);
            item.setQuantity(quantity);

            InventoryItem saved = inventoryRepo.save(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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