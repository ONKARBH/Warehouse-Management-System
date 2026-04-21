package com.wms.warehouse.repository;

import com.wms.warehouse.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    @Query("SELECT i FROM InventoryItem i WHERE i.product.sku = :sku")
    List<InventoryItem> findByProductSku(@Param("sku") String sku);

    @Query("SELECT i FROM InventoryItem i WHERE i.storageBin.binCode = :binCode")
    List<InventoryItem> findByStorageBin_BinCode(@Param("binCode") String binCode);

    // CRITICAL: This method MUST exist for ReceivingService
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryItem i WHERE i.product.sku = :sku AND i.storageBin.binCode = :binCode")
    Optional<InventoryItem> findByProductSkuAndBinCodeWithLock(@Param("sku") String sku,
                                                               @Param("binCode") String binCode);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM InventoryItem i WHERE i.product.sku = :sku")
    Integer getTotalStockBySku(@Param("sku") String sku);

    List<InventoryItem> findByProductId(Long productId);
    List<InventoryItem> findByStorageBinId(Long binId);
    List<InventoryItem> findByWarehouseId(Long warehouseId);
}