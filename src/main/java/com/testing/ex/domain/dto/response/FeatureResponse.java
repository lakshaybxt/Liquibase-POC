package com.testing.ex.domain.dto.response;

/**
 * DTO representing the response for feature details.
 *
 * @param ram the RAM specification
 * @param storage the storage specification
 * @param battery the battery specification
 */
public record FeatureResponse(
    String ram,
    String storage,
    String battery
) {
}
