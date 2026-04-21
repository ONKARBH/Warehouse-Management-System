package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // Find warehouse by unique code
    Optional<Warehouse> findByCode(String code);

    // Check if warehouse exists by code
    boolean existsByCode(String code);

    // Find warehouses by city
    List<Warehouse> findByCity(String city);

    // Find warehouses by country
    List<Warehouse> findByCountry(String country);

    // Search warehouses by name (contains)
    List<Warehouse> findByNameContainingIgnoreCase(String name);
}