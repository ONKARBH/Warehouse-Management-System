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

    Optional<StorageBin> findByBinCode(String binCode);

    List<StorageBin> findByAisleId(Long aisleId);

    boolean existsByBinCode(String binCode);

    // FIXED: These methods must exist
    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy < b.maxCapacity")
    List<StorageBin> findAvailableBins();

    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy = 0 OR b.currentOccupancy IS NULL")
    List<StorageBin> findEmptyBins();

    @Query("SELECT b FROM StorageBin b WHERE b.aisle.zone.warehouse.id = :warehouseId")
    List<StorageBin> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    List<StorageBin> findByShelfLevel(Integer shelfLevel);
}