package com.wms.warehouse.repository;

import com.wms.warehouse.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Find movements by product SKU
    @Query("SELECT m FROM StockMovement m WHERE m.product.sku = :sku ORDER BY m.timestamp DESC")
    List<StockMovement> findByProductSku(@Param("sku") String sku);

    // Find movements by bin code
    @Query("SELECT m FROM StockMovement m WHERE m.storageBin.binCode = :binCode ORDER BY m.timestamp DESC")
    List<StockMovement> findByBinCode(@Param("binCode") String binCode);

    // Find movements by reference number (PO, Order ID)
    List<StockMovement> findByReferenceNumber(String referenceNumber);

    // Find movements by type
    List<StockMovement> findByMovementType(StockMovement.MovementType type);

    // Find movements between dates
    List<StockMovement> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Find movements by user
    List<StockMovement> findByUserId(String userId);

    // Get recent movements (last 24 hours)
    @Query("SELECT m FROM StockMovement m WHERE m.timestamp >= :since ORDER BY m.timestamp DESC")
    List<StockMovement> findRecentMovements(@Param("since") LocalDateTime since);
}