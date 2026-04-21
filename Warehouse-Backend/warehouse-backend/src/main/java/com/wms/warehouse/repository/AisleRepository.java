package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Aisle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AisleRepository extends JpaRepository<Aisle, Long> {

    // Find aisles by zone ID
    List<Aisle> findByZoneId(Long zoneId);

    // Find aisles by zone code
    @Query("SELECT a FROM Aisle a WHERE a.zone.zoneCode = :zoneCode")
    List<Aisle> findByZoneCode(@Param("zoneCode") String zoneCode);

    // Find aisles by warehouse ID
    @Query("SELECT a FROM Aisle a WHERE a.zone.warehouse.id = :warehouseId")
    List<Aisle> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    // Find aisle by aisle number and zone
    Optional<Aisle> findByAisleNumberAndZoneId(String aisleNumber, Long zoneId);

    // Find aisles by aisle number containing
    List<Aisle> findByAisleNumberContainingIgnoreCase(String aisleNumber);
}