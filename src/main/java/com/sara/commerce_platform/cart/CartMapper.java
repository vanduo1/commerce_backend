package com.sara.commerce_platform.cart;

import com.sara.commerce_platform.cart.dto.CartResponse;
import com.sara.commerce_platform.cart.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toResponse (Cart cart);
}
