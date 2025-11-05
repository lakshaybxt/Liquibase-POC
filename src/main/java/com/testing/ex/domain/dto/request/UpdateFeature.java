package com.testing.ex.domain.dto.request;

/**
 * DTO for updating feature specifications.
 *
 * @param ram     the RAM specification
 * @param storage the storage specification
 * @param battery the battery specification
 */
public record UpdateFeature(
    String ram,
    String storage,
    String battery
) {
}
