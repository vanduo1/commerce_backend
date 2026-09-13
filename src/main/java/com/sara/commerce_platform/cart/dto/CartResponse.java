package com.sara.commerce_platform.cart.dto;

import com.sara.commerce_platform.cart.entity.CartItem;

import java.util.List;

public record CartResponse(List<CartItem> items) {
}
