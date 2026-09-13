package com.sara.commerce_platform.payment.dto;

public record MomoRequest(String partnerCode,
        String partnerName,
        String storeId,
        String requestId,
        String amount,
        String orderId,
         String orderInfo,
         String redirectUrl,
         String ipnUrl,
         String lang,
         String extraData,
         String requestType,
         String signature) {
}
