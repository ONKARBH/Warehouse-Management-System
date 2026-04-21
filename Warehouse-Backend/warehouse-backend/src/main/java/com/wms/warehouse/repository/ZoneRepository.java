package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    // Find zones by warehouse ID
    List<Zone> findByWarehouseId(Long warehouseId);

    // Find zones by warehouse code
    @Query("SELECT z FROM Zone z WHERE z.warehouse.code = :warehouseCode")
    List<Zone> findByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    // Find zone by zone code
    Optional<Zone> findByZoneCode(String zoneCode);

    // Find zones by name containing
    List<Zone> findByNameContainingIgnoreCase(String name);

    // Check if zone exists by code
    boolean existsByZoneCode(String zoneCode);
}