package com.sara.commerce_platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "momo")
public record MomoConfig(
        // kebab-case to camelCase
        String partnerCode,
        String secretKey,
        String accessKey,
        String endpoint,
        String returnUrl,
        String notifyUrl

) {
}
