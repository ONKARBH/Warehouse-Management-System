package com.wms.warehouse.repository;

import com.wms.warehouse.entity.StorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorageBinRepository extends JpaRepository<StorageBin, Long> {

    // Find bin by unique bin code
    Optional<StorageBin> findByBinCode(String binCode);

    // Find bins by aisle ID
    List<StorageBin> findByAisleId(Long aisleId);

    // FIXED: Find bins with available space (currentOccupancy < maxCapacity)
    // Using @Query instead of method name
    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy < b.maxCapacity")
    List<StorageBin> findAvailableBins();

    // FIXED: Find empty bins (currentOccupancy = 0)
    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy = 0")
    List<StorageBin> findEmptyBins();

    // Find bins by warehouse ID
    @Query("SELECT b FROM StorageBin b WHERE b.aisle.zone.warehouse.id = :warehouseId")
    List<StorageBin> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    // Find bins by warehouse code
    @Query("SELECT b FROM StorageBin b WHERE b.aisle.zone.warehouse.code = :warehouseCode")
    List<StorageBin> findByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    // Find bins with specific shelf level
    List<StorageBin> findByShelfLevel(Integer shelfLevel);

    // Find best available bin (most space)
    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy < b.maxCapacity ORDER BY (b.maxCapacity - b.currentOccupancy) DESC")
    List<StorageBin> findBestAvailableBins();

    // Check if bin exists by code
    boolean existsByBinCode(String binCode);

    // Find bins with occupancy less than specific value
    List<StorageBin> findByCurrentOccupancyLessThan(Integer occupancy);

    // Find bins with max capacity greater than specific value
    List<StorageBin> findByMaxCapacityGreaterThan(Integer capacity);
}