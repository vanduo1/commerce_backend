package com.sara.commerce_platform.payment.dto;

public record MomoResponse(
        String partnerCode,
        String orderId,
        String requestId,
        long amount,
        int resultCode,
        String message,
        String payUrl
) {}