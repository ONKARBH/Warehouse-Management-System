package com.wms.warehouse.controller;

import com.wms.warehouse.dto.PutawaySuggestionDTO;
import com.wms.warehouse.dto.ReceiveShipmentDTO;
import com.wms.warehouse.dto.ReceivingResponseDTO;
import com.wms.warehouse.service.PutawayService;
import com.wms.warehouse.service.ReceivingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receiving")
public class ReceivingController {

    private final ReceivingService receivingService;
    private final PutawayService putawayService;

    public ReceivingController(ReceivingService receivingService,
                               PutawayService putawayService) {
        this.receivingService = receivingService;
        this.putawayService = putawayService;
    }

    /**
     * Suggest best bin for incoming product
     * Use this BEFORE receiving to know where to put items
     */
    @PostMapping("/suggest-bin")
    public ResponseEntity<PutawaySuggestionDTO> suggestBin(@RequestBody SuggestBinRequest request) {
        PutawaySuggestionDTO suggestion = putawayService.findOptimalBin(
                request.getProductSku(),
                request.getQuantity()
        );
        return ResponseEntity.ok(suggestion);
    }

    /**
     * Receive shipment (CRITICAL ENDPOINT FOR WEEK 2)
     * This is an ATOMIC operation - all or nothing
     */
    @PostMapping("/receive")
    public ResponseEntity<ReceivingResponseDTO> receiveShipment(@Valid @RequestBody ReceiveShipmentDTO request) {
        ReceivingResponseDTO response = receivingService.receiveShipment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Quick receive with minimal fields
     */
    @PostMapping("/quick-receive")
    public ResponseEntity<ReceivingResponseDTO> quickReceive(
            @RequestParam String productSku,
            @RequestParam Integer quantity,
            @RequestParam String referenceNumber) {

        ReceiveShipmentDTO request = new ReceiveShipmentDTO(productSku, quantity, referenceNumber);
        ReceivingResponseDTO response = receivingService.receiveShipment(request);
        return ResponseEntity.ok(response);
    }

    // Inner class for suggest-bin request
    static class SuggestBinRequest {
        private String productSku;
        private Integer quantity;

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}