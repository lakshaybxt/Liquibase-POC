package com.testing.ex.domain.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for updating a product.
 *
 * @param name the name of the product
 * @param sku the stock keeping unit of the product
 * @param price  the price of the product
 * @param description the description of the product
 * @param category the category of the product
 * @param features the features of the product
 */
@Builder
public record UpdateProductRequest(
        String name,
        String sku,
        String category,
        BigDecimal price,
        String description,
        Map<String, String> features
) {
}
