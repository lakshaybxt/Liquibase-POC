package com.testing.ex.domain.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for product response.
 *
 * @param id the product ID
 * @param name the product name
 * @param sku the product SKU
 * @param category the product category
 * @param price the product price
 * @param description the product description
 * @param features the product features
 */
@Builder
public record ProductResponse(
    Long id,
    String name,
    String sku,
    String category,
    BigDecimal price,
    String description,
    Map<String, String> features
) {
}
