package com.testing.ex.service;

import com.testing.ex.domain.dto.request.CreateProductRequest;
import com.testing.ex.domain.dto.request.UpdateProductRequest;
import com.testing.ex.domain.dto.response.ProductResponse;
import com.testing.ex.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service layer contract for product related operations. Methods are tenant-
 * aware and accept a userId/tenantId where relevant.
 */
public interface ProductService {
  /**
   * Get paginated products for the given tenant (user).
   *
   * @param userId   tenant/user identifier
   * @param pageable pagination and sort information
   * @return page of {@link ProductResponse}
   */
  Page<ProductResponse> getAllByUserId(String userId, Pageable pageable);

  /**
   * Create a new product for the specified tenant.
   *
   * @param userId  tenant identifier
   * @param request create payload
   * @return created {@link ProductResponse}
   */
  ProductResponse createProduct(String userId, CreateProductRequest request);

  /**
   * Update an existing product owned by the tenant.
   *
   * @param userId    tenant identifier
   * @param productId product numeric id
   * @param request   update payload
   * @return updated {@link ProductResponse}
   */
  ProductResponse updateProduct(String userId, Long productId, UpdateProductRequest request);

  /**
   * Delete a product owned by the tenant.
   *
   * @param userId    tenant identifier
   * @param productId product numeric id
   */
  void deleteProduct(String userId, Long productId);

  /**
   * Fetch a product entity by id and user/tenant id (used when encryption or
   * full entity is needed).
   */
  Product getProductEntityByIdAndUserId(Long productId, String userId);
}
