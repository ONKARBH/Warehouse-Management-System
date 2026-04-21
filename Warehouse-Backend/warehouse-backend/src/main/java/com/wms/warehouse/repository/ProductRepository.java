package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find product by SKU (most important)
    Optional<Product> findBySku(String sku);

    // Check if product exists by SKU
    boolean existsBySku(String sku);

    // Find products by category
    List<Product> findByCategory(String category);

    // Find products by name (contains - search)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products by price range
    List<Product> findByPriceBetween(Double min, Double max);

    // Find products with low stock (across all bins)
    @Query("SELECT p FROM Product p WHERE (SELECT COALESCE(SUM(i.quantity), 0) FROM InventoryItem i WHERE i.product = p) <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

    // Get total value of all inventory
    @Query("SELECT COALESCE(SUM(p.price * i.quantity), 0) FROM Product p JOIN InventoryItem i ON i.product = p")
    Double getTotalInventoryValue();
}