package com.sara.commerce_platform.payment.dto;

public record PaymentWebhookRequest(String orderId, String transactionId, String status) {
}
