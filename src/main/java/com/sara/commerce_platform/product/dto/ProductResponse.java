package com.sara.commerce_platform.product.dto;

import java.math.BigDecimal;

public record ProductResponse(String name, BigDecimal price, Integer stock) {
}
