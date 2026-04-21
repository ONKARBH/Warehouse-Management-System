package com.wms.warehouse.service;

import com.wms.warehouse.dto.ReceiveShipmentDTO;
import com.wms.warehouse.dto.ReceivingResponseDTO;
import com.wms.warehouse.dto.PutawaySuggestionDTO;
import com.wms.warehouse.entity.*;
import com.wms.warehouse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ReceivingService {

    private final InventoryItemRepository inventoryRepo;
    private final ProductRepository productRepo;
    private final StorageBinRepository binRepository;
    private final StockMovementRepository movementRepo;
    private final PutawayService putawayService;

    public ReceivingService(InventoryItemRepository inventoryRepo,
                            ProductRepository productRepo,
                            StorageBinRepository binRepository,
                            StockMovementRepository movementRepo,
                            PutawayService putawayService) {
        this.inventoryRepo = inventoryRepo;
        this.productRepo = productRepo;
        this.binRepository = binRepository;
        this.movementRepo = movementRepo;
        this.putawayService = putawayService;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReceivingResponseDTO receiveShipment(ReceiveShipmentDTO request) {

        System.out.println("=== RECEIVING SHIPMENT ===");
        System.out.println("Product SKU: " + request.getProductSku());
        System.out.println("Quantity: " + request.getQuantity());
        System.out.println("Reference: " + request.getReferenceNumber());

        // STEP 1: Find the product
        Product product = productRepo.findBySku(request.getProductSku())
                .orElseThrow(() -> new RuntimeException("Product not found: " + request.getProductSku()));

        // STEP 2: Determine target bin
        String targetBinCode = "";

        if (targetBinCode == null || targetBinCode.isEmpty()) {
            PutawaySuggestionDTO suggestion = putawayService.findOptimalBin(request.getProductSku(), request.getQuantity());
            targetBinCode = suggestion.getSuggestedBinCode();
            System.out.println("System suggested bin: " + targetBinCode);
        } else {
            targetBinCode = request.getTargetBinCode();
        }

        // STEP 3: Find the storage bin
        String finalTargetBinCode = targetBinCode;
        StorageBin targetBin = binRepository.findByBinCode(targetBinCode)
                .orElseThrow(() -> new RuntimeException("Bin not found: " + finalTargetBinCode));

        // STEP 4: Find or create inventory item
        InventoryItem inventoryItem = inventoryRepo
                .findByProductSkuAndBinCodeWithLock(request.getProductSku(), targetBinCode)
                .orElse(null);

        int oldQuantity = 0;

        if (inventoryItem == null) {
            inventoryItem = new InventoryItem();
            inventoryItem.setProduct(product);
            inventoryItem.setStorageBin(targetBin);
            inventoryItem.setWarehouse(targetBin.getWarehouse());
            inventoryItem.setQuantity(0);
            oldQuantity = 0;
        } else {
            oldQuantity = inventoryItem.getQuantity();
        }

        // STEP 5: Update quantity
        int newQuantity = oldQuantity + request.getQuantity();
        inventoryItem.setQuantity(newQuantity);
        inventoryItem.setLastUpdated(LocalDateTime.now());

        // STEP 6: Save inventory item
        inventoryRepo.save(inventoryItem);

        // STEP 7: Record stock movement
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setStorageBin(targetBin);
        movement.setWarehouse(targetBin.getWarehouse());
        movement.setMovementType(StockMovement.MovementType.RECEIVED);
        movement.setQuantityChange(request.getQuantity());
        movement.setQuantityBefore(oldQuantity);
        movement.setQuantityAfter(newQuantity);
        movement.setReferenceNumber(request.getReferenceNumber());
        movement.setUserId("SYSTEM");
        movement.setNotes(request.getNotes());
        movementRepo.save(movement);

        // STEP 8: Update bin occupancy - FIXED
        int currentOcc = targetBin.getCurrentOccupancy() != null ? targetBin.getCurrentOccupancy() : 0;
        targetBin.setCurrentOccupancy(currentOcc + request.getQuantity());
        binRepository.save(targetBin);

        System.out.println("✓ Received " + request.getQuantity() + " units of " + product.getSku());
        System.out.println("  Bin " + targetBinCode + " now has " + newQuantity + " units");
        System.out.println("=== RECEIVING COMPLETE ===");

        // STEP 9: Build response
        ReceivingResponseDTO response = new ReceivingResponseDTO();
        response.setStatus("SUCCESS");
        response.setProductSku(product.getSku());
        response.setProductName(product.getName());
        response.setBinCode(targetBinCode);
        response.setQuantityReceived(request.getQuantity());
        response.setNewTotalQuantity(newQuantity);
        response.setReferenceNumber(request.getReferenceNumber());
        response.setTimestamp(LocalDateTime.now());
        response.setMessage("Successfully received " + request.getQuantity() + " units into bin " + targetBinCode);

        return response;
    }
}