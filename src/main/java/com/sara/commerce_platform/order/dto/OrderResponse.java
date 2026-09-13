package com.sara.commerce_platform.order.dto;

import com.sara.commerce_platform.order.entity.OrderItem;
import com.sara.commerce_platform.payment.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(List<OrderItem> items, BigDecimal totalPrice, Payment payment) {
}
