package com.sara.commerce_platform.order;

import com.sara.commerce_platform.order.dto.OrderResponse;
import com.sara.commerce_platform.order.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toResponse(Order order);
}
