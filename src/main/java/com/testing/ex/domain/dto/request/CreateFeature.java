package com.testing.ex.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for creating a new feature.
 *
 * @param ram     the RAM specification of the feature
 * @param storage the storage specification of the feature
 * @param battery the battery specification of the feature
 */
public record CreateFeature(
    @NotBlank String ram,
    @NotBlank String storage,
    @NotBlank String battery
) {
}
