package com.sara.commerce_platform.product.dto;

import java.math.BigDecimal;

public record ProductUpdateRequest(BigDecimal price, Integer stock) {
}
