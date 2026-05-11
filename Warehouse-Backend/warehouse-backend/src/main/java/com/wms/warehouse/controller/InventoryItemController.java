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
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> getAllInventory() {
        List<InventoryItem> items = inventoryRepo.findAll();

        // Convert to DTO with explicit data
        return items.stream().map(item -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", item.getId());
            dto.put("quantity", item.getQuantity());

            // Add product details
            if (item.getProduct() != null) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", item.getProduct().getId());
                productMap.put("sku", item.getProduct().getSku());
                productMap.put("name", item.getProduct().getName());
                dto.put("product", productMap);
            }

            // Add storage bin details
            if (item.getStorageBin() != null) {
                Map<String, Object> binMap = new HashMap<>();
                binMap.put("id", item.getStorageBin().getId());
                binMap.put("binCode", item.getStorageBin().getBinCode());
                dto.put("storageBin", binMap);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/product/{sku}")
    public List<Map<String, Object>> getInventoryByProduct(@PathVariable String sku) {
        List<InventoryItem> items = inventoryRepo.findByProductSku(sku);

        return items.stream().map(item -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", item.getId());
            dto.put("quantity", item.getQuantity());

            if (item.getProduct() != null) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", item.getProduct().getId());
                productMap.put("sku", item.getProduct().getSku());
                productMap.put("name", item.getProduct().getName());
                dto.put("product", productMap);
            }

            if (item.getStorageBin() != null) {
                Map<String, Object> binMap = new HashMap<>();
                binMap.put("id", item.getStorageBin().getId());
                binMap.put("binCode", item.getStorageBin().getBinCode());
                dto.put("storageBin", binMap);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/bin/{binCode}")
    public List<Map<String, Object>> getInventoryByBin(@PathVariable String binCode) {
        List<InventoryItem> items = inventoryRepo.findByStorageBin_BinCode(binCode);

        return items.stream().map(item -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", item.getId());
            dto.put("quantity", item.getQuantity());

            if (item.getProduct() != null) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", item.getProduct().getId());
                productMap.put("sku", item.getProduct().getSku());
                productMap.put("name", item.getProduct().getName());
                dto.put("product", productMap);
            }

            if (item.getStorageBin() != null) {
                Map<String, Object> binMap = new HashMap<>();
                binMap.put("id", item.getStorageBin().getId());
                binMap.put("binCode", item.getStorageBin().getBinCode());
                dto.put("storageBin", binMap);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createInventoryItem(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Integer> productMap = (Map<String, Integer>) request.get("product");
            Map<String, Integer> binMap = (Map<String, Integer>) request.get("storageBin");
            Map<String, Integer> warehouseMap = (Map<String, Integer>) request.get("warehouse");
            Integer quantity = (Integer) request.get("quantity");

            Long productId = productMap.get("id").longValue();
            Long binId = binMap.get("id").longValue();
            Long warehouseId = warehouseMap.get("id").longValue();

            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
            StorageBin bin = binRepo.findById(binId)
                    .orElseThrow(() -> new RuntimeException("Bin not found with id: " + binId));
            Warehouse warehouse = warehouseRepo.findById(warehouseId)
                    .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + warehouseId));

            InventoryItem item = new InventoryItem();
            item.setProduct(product);
            item.setStorageBin(bin);
            item.setWarehouse(warehouse);
            item.setQuantity(quantity);

            InventoryItem saved = inventoryRepo.save(item);

            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("quantity", saved.getQuantity());

            Map<String, Object> productRes = new HashMap<>();
            productRes.put("id", product.getId());
            productRes.put("sku", product.getSku());
            productRes.put("name", product.getName());
            response.put("product", productRes);

            Map<String, Object> binRes = new HashMap<>();
            binRes.put("id", bin.getId());
            binRes.put("binCode", bin.getBinCode());
            response.put("storageBin", binRes);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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