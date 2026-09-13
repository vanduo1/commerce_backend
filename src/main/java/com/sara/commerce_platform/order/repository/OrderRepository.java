package com.sara.commerce_platform.order.repository;

import com.sara.commerce_platform.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByIdAndUserId(String orderId, String userId);

    List<Order> findAllByUserId(String userId);
}
