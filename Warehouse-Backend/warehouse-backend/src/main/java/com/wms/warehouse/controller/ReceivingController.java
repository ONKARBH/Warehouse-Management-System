package com.wms.warehouse.controller;

import com.wms.warehouse.dto.PutawaySuggestionDTO;
import com.wms.warehouse.dto.ReceiveShipmentDTO;
import com.wms.warehouse.dto.ReceivingResponseDTO;
import com.wms.warehouse.service.PutawayService;
import com.wms.warehouse.service.ReceivingService;
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

    @PostMapping("/suggest-bin")
    public ResponseEntity<PutawaySuggestionDTO> suggestBin(@RequestBody SuggestBinRequest request) {
        PutawaySuggestionDTO suggestion = putawayService.findOptimalBin(
                request.getProductSku(),
                request.getQuantity()
        );
        return ResponseEntity.ok(suggestion);
    }

    @PostMapping("/receive")
    public ResponseEntity<ReceivingResponseDTO> receiveShipment(@RequestBody ReceiveShipmentDTO request) {
        System.out.println("Received request: " + request);
        ReceivingResponseDTO response = receivingService.receiveShipment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quick-receive")
    public ResponseEntity<ReceivingResponseDTO> quickReceive(
            @RequestParam String productSku,
            @RequestParam Integer quantity,
            @RequestParam String referenceNumber) {

        ReceiveShipmentDTO request = new ReceiveShipmentDTO();
        request.setProductSku(productSku);
        request.setQuantity(quantity);
        request.setReferenceNumber(referenceNumber);

        ReceivingResponseDTO response = receivingService.receiveShipment(request);
        return ResponseEntity.ok(response);
    }

    static class SuggestBinRequest {
        private String productSku;
        private Integer quantity;

        public String getProductSku() { return productSku; }
        public void setProductSku(String productSku) { this.productSku = productSku; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}