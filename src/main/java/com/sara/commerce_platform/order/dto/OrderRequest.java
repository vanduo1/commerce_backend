package com.sara.commerce_platform.order.dto;

import java.util.List;

public record OrderRequest(List<String> cartItemIds) {
}
