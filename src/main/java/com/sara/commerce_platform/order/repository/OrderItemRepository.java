package com.sara.commerce_platform.order.repository;

import com.sara.commerce_platform.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
}
