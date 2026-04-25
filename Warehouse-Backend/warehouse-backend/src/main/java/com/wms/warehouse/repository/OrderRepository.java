package com.wms.warehouse.repository;

import com.wms.warehouse.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByState(Order.OrderState state);
    List<Order> findByCustomerEmail(String email);
}