    package com.wms.warehouse.controller;

    import com.wms.warehouse.entity.Warehouse;
    import com.wms.warehouse.repository.WarehouseRepository;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;

    @RestController
    @RequestMapping("/api/warehouses")
    public class WarehouseController {

        private final WarehouseRepository warehouseRepository;

        public WarehouseController(WarehouseRepository warehouseRepository) {
            this.warehouseRepository = warehouseRepository;
        }

        // GET all warehouses
        @GetMapping
        public List<Warehouse> getAllWarehouses() {
            return warehouseRepository.findAll();
        }

        // GET warehouse by ID
        @GetMapping("/{id}")
        public ResponseEntity<Warehouse> getWarehouseById(@PathVariable Long id) {
            return warehouseRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        // GET warehouse by code
        @GetMapping("/code/{code}")
        public ResponseEntity<Warehouse> getWarehouseByCode(@PathVariable String code) {
            return warehouseRepository.findByCode(code)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        // POST create new warehouse
        @PostMapping
        public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse warehouse) {
            // Check if code already exists
            if (warehouseRepository.existsByCode(warehouse.getCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Warehouse saved = warehouseRepository.save(warehouse);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }

        // PUT update warehouse
        @PutMapping("/{id}")
        public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Long id,
                                                         @RequestBody Warehouse warehouseDetails) {
            return warehouseRepository.findById(id)
                    .map(warehouse -> {
                        warehouse.setName(warehouseDetails.getName());
                        warehouse.setCode(warehouseDetails.getCode());
                        warehouse.setAddress(warehouseDetails.getAddress());
                        warehouse.setCity(warehouseDetails.getCity());
                        warehouse.setCountry(warehouseDetails.getCountry());
                        return ResponseEntity.ok(warehouseRepository.save(warehouse));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }

        // DELETE warehouse
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
            if (!warehouseRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            warehouseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        // GET warehouses by city
        @GetMapping("/city/{city}")
        public List<Warehouse> getWarehousesByCity(@PathVariable String city) {
            return warehouseRepository.findByCity(city);
        }
    }