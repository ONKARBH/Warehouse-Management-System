package com.wms.warehouse.controller;

import com.wms.warehouse.service.BarcodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/barcode")
public class BarcodeController {

    private final BarcodeService barcodeService;

    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @GetMapping(value = "/product/{sku}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getProductBarcode(@PathVariable String sku) {
        try {
            byte[] barcode = barcodeService.generateBarcode(sku, 300, 100);
            return ResponseEntity.ok().body(barcode);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/qrcode/{sku}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getProductQRCode(@PathVariable String sku) {
        try {
            byte[] qrcode = barcodeService.generateQRCode(sku, 200, 200);
            return ResponseEntity.ok().body(qrcode);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}