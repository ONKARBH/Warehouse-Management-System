package com.wms.warehouse.repository;

import com.wms.warehouse.entity.StorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorageBinRepository extends JpaRepository<StorageBin, Long> {
    Optional<StorageBin> findByBinCode(String binCode);
    boolean existsByBinCode(String binCode);

    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy < b.maxCapacity")
    List<StorageBin> findAvailableBins();

    List<StorageBin> findByAisleId(Long aisleId);
}