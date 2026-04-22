package com.wms.warehouse.service;

import com.wms.warehouse.entity.InventoryItem;
import com.wms.warehouse.entity.StorageBin;
import com.wms.warehouse.dto.PutawaySuggestionDTO;
import com.wms.warehouse.repository.InventoryItemRepository;
import com.wms.warehouse.repository.StorageBinRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PutawayService {

    private final InventoryItemRepository inventoryRepo;
    private final StorageBinRepository binRepository;

    public PutawayService(InventoryItemRepository inventoryRepo,
                          StorageBinRepository binRepository) {
        this.inventoryRepo = inventoryRepo;
        this.binRepository = binRepository;
    }

    public PutawaySuggestionDTO findOptimalBin(String productSku, Integer quantity) {

        System.out.println("Finding optimal bin for: " + productSku + ", quantity: " + quantity);

        // STRATEGY 1: Find bin that already has this product
        List<InventoryItem> existingStock = inventoryRepo.findByProductSku(productSku);

        if (existingStock != null && !existingStock.isEmpty()) {
            for (InventoryItem item : existingStock) {
                StorageBin bin = item.getStorageBin();
                if (bin != null) {
                    int maxCap = bin.getMaxCapacity() != null ? bin.getMaxCapacity() : 0;
                    int currOcc = bin.getCurrentOccupancy() != null ? bin.getCurrentOccupancy() : 0;
                    int availableSpace = maxCap - currOcc;

                    if (availableSpace >= quantity) {
                        System.out.println("Found existing bin: " + bin.getBinCode() + " with space: " + availableSpace);
                        return new PutawaySuggestionDTO(
                                bin.getBinCode(),
                                availableSpace,
                                "Consolidate with existing stock (already has " + item.getQuantity() + " units)"
                        );
                    }
                }
            }
        }

        // STRATEGY 2: Get all bins and find first with enough space
        List<StorageBin> allBins = binRepository.findAll();

        if (allBins != null && !allBins.isEmpty()) {
            for (StorageBin bin : allBins) {
                int maxCap = bin.getMaxCapacity() != null ? bin.getMaxCapacity() : 0;
                int currOcc = bin.getCurrentOccupancy() != null ? bin.getCurrentOccupancy() : 0;
                int availableSpace = maxCap - currOcc;

                if (availableSpace >= quantity) {
                    System.out.println("Found available bin: " + bin.getBinCode() + " with space: " + availableSpace);
                    return new PutawaySuggestionDTO(
                            bin.getBinCode(),
                            availableSpace,
                            "Bin with available space"
                    );
                }
            }
        }

        // No bin found
        System.out.println("No suitable bin found!");
        throw new RuntimeException("No suitable bin found for " + quantity + " units of product " + productSku);
    }
}