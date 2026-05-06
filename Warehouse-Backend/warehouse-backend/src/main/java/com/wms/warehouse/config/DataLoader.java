package com.wms.warehouse.config;

import com.wms.warehouse.entity.*;
import com.wms.warehouse.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final WarehouseRepository warehouseRepo;
    private final ZoneRepository zoneRepo;
    private final AisleRepository aisleRepo;
    private final StorageBinRepository binRepo;
    private final ProductRepository productRepo;
    private final InventoryItemRepository inventoryRepo;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(WarehouseRepository warehouseRepo,
                      ZoneRepository zoneRepo,
                      AisleRepository aisleRepo,
                      StorageBinRepository binRepo,
                      ProductRepository productRepo,
                      InventoryItemRepository inventoryRepo,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.warehouseRepo = warehouseRepo;
        this.zoneRepo = zoneRepo;
        this.aisleRepo = aisleRepo;
        this.binRepo = binRepo;
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== LOADING DATA ===");

        // Create Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@wms.com");
            admin.setFullName("System Administrator");
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✓ Created Admin: admin/admin123");
        }

        // Create Operator User
        if (!userRepository.existsByUsername("operator")) {
            User operator = new User();
            operator.setUsername("operator");
            operator.setPassword(passwordEncoder.encode("operator123"));
            operator.setEmail("operator@wms.com");
            operator.setFullName("Floor Operator");
            operator.setRole(User.Role.OPERATOR);
            operator.setEnabled(true);
            userRepository.save(operator);
            System.out.println("✓ Created Operator: operator/operator123");
        }

        System.out.println("=== DATA LOADED ===");
    }
}