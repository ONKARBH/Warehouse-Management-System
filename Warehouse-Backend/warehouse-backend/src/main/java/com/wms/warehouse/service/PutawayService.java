package com.wms.warehouse.service;

import com.wms.warehouse.entity.InventoryItem;
import com.wms.warehouse.entity.StorageBin;
import com.wms.warehouse.dto.PutawaySuggestionDTO;
import com.wms.warehouse.repository.InventoryItemRepository;
import com.wms.warehouse.repository.StorageBinRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
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

        // STRATEGY 1: Find bin that already has this product
        List<InventoryItem> existingStock = inventoryRepo.findByProductSku(productSku);

        for (InventoryItem item : existingStock) {
            StorageBin bin = item.getStorageBin();

            // Safe null handling
            int maxCapacity = getSafeValue(bin.getMaxCapacity());
            int currentOccupancy = getSafeValue(bin.getCurrentOccupancy());
            int availableSpace = maxCapacity - currentOccupancy;

            if (availableSpace >= quantity) {
                return new PutawaySuggestionDTO(
                        bin.getBinCode(),
                        availableSpace,
                        "Consolidate with existing stock (already has " + item.getQuantity() + " units)"
                );
            }
        }

        // STRATEGY 2: Find empty bin
        List<StorageBin> emptyBins = binRepository.findEmptyBins();
        if (emptyBins != null && !emptyBins.isEmpty()) {
            StorageBin bestEmptyBin = emptyBins.get(0);
            int maxCapacity = getSafeValue(bestEmptyBin.getMaxCapacity());
            return new PutawaySuggestionDTO(
                    bestEmptyBin.getBinCode(),
                    maxCapacity,
                    "Empty bin available"
            );
        }

        // STRATEGY 3: Find any bin with enough space
        List<StorageBin> availableBins = binRepository.findAvailableBins();
        if (availableBins != null && !availableBins.isEmpty()) {

            // Sort by available space (largest first)
            availableBins.sort((bin1, bin2) -> {
                int space1 = getSafeValue(bin1.getMaxCapacity()) - getSafeValue(bin1.getCurrentOccupancy());
                int space2 = getSafeValue(bin2.getMaxCapacity()) - getSafeValue(bin2.getCurrentOccupancy());
                return Integer.compare(space2, space1);
            });

            StorageBin bestBin = availableBins.get(0);
            int maxCapacity = getSafeValue(bestBin.getMaxCapacity());
            int currentOccupancy = getSafeValue(bestBin.getCurrentOccupancy());
            int availableSpace = maxCapacity - currentOccupancy;

            if (availableSpace >= quantity) {
                return new PutawaySuggestionDTO(
                        bestBin.getBinCode(),
                        availableSpace,
                        "Bin with available space (current occupancy: " + currentOccupancy + ")"
                );
            }
        }

        throw new RuntimeException("No suitable bin found for " + quantity + " units of product " + productSku);
    }

    // Helper method to handle null Integer values
    private int getSafeValue(Integer value) {
        return value != null ? value : 0;
    }
}