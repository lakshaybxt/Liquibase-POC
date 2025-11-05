package com.testing.ex.repos;

import com.testing.ex.domain.entity.Product;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for tenant-scoped Product persistence operations.
 *
 * <p>The repository exposes convenience finder methods that always include the
 * tenant identifier (tenantId) to ensure multi-tenant isolation at the data
 * access layer.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Find all products belonging to the given tenant.
   *
   * @param userId tenant identifier (typically the authenticated user's id)
   * @return page of products for the tenant
   */
  Page<Product> findAllByTenantId(String userId, Pageable pageable);

  /**
   * Find a product by its id and tenant id.
   *
   * @param id       the product numeric id
   * @param tenantId the tenant identifier
   * @return optional product if found and belongs to tenant
   */
  Optional<Product> findByIdAndTenantId(Long id, String tenantId);

  /**
   * Delete a product by id only if it belongs to the given tenant.
   *
   * @param id       the product numeric id
   * @param tenantId the tenant identifier
   */
  void deleteByIdAndTenantId(Long id, String tenantId);
}
