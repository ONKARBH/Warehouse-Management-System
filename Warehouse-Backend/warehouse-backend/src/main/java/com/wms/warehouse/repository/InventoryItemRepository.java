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

    // Find inventory by product SKU
    @Query("SELECT i FROM InventoryItem i WHERE i.product.sku = :sku")
    List<InventoryItem> findByProductSku(@Param("sku") String sku);

    // Find inventory by bin code
    @Query("SELECT i FROM InventoryItem i WHERE i.storageBin.binCode = :binCode")
    List<InventoryItem> findByStorageBin_BinCode(@Param("binCode") String binCode);

    // Find inventory by warehouse code
    @Query("SELECT i FROM InventoryItem i WHERE i.warehouse.code = :warehouseCode")
    List<InventoryItem> findByWarehouseCode(@Param("warehouseCode") String warehouseCode);

    // Find specific product in specific bin
    @Query("SELECT i FROM InventoryItem i WHERE i.product.sku = :sku AND i.storageBin.binCode = :binCode")
    Optional<InventoryItem> findByProductSkuAndStorageBin_BinCode(@Param("sku") String sku,
                                                                  @Param("binCode") String binCode);

    // WITH LOCK - For preventing race conditions (CRITICAL for Week 2)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryItem i WHERE i.product.sku = :sku AND i.storageBin.binCode = :binCode")
    Optional<InventoryItem> findByProductSkuAndBinCodeWithLock(@Param("sku") String sku,
                                                               @Param("binCode") String binCode);

    // Get total stock of a product across all bins
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM InventoryItem i WHERE i.product.sku = :sku")
    Integer getTotalStockBySku(@Param("sku") String sku);

    // Check if product exists in a specific bin
    @Query("SELECT COUNT(i) > 0 FROM InventoryItem i WHERE i.product.sku = :sku AND i.storageBin.binCode = :binCode")
    boolean existsByProductSkuAndStorageBin_BinCode(@Param("sku") String sku,
                                                    @Param("binCode") String binCode);

    // Find all inventory items with low stock (less than threshold)
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= :threshold")
    List<InventoryItem> findLowStockItems(@Param("threshold") Integer threshold);

    // Find inventory by product ID
    List<InventoryItem> findByProductId(Long productId);

    // Find inventory by bin ID
    List<InventoryItem> findByStorageBinId(Long binId);

    // Find inventory by warehouse ID
    List<InventoryItem> findByWarehouseId(Long warehouseId);
}