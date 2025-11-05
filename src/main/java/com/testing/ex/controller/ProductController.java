package com.testing.ex.controller;

import com.testing.ex.domain.dto.request.CreateProductRequest;
import com.testing.ex.domain.dto.request.UpdateProductRequest;
import com.testing.ex.domain.dto.response.ProductResponse;
import com.testing.ex.domain.entity.Product;
import com.testing.ex.service.ProductService;
import com.testing.ex.utils.EncryptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST controller that exposes CRUD endpoints for product products.
 *
 * <p>Endpoints are tenant-aware: the authenticated user's id is read from the
 * request attribute "userId" (populated by the JWT security filter) and is used
 * as the tenant identifier when creating, querying, updating, or deleting
 * products.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations for managing products (tenant-scoped)")
public class ProductController {

  private final ProductService productService;
  private final EncryptionUtils encryptionUtils;

  /**
   * Get all products for the current authenticated user (tenant).
   *
   * @param userId the authenticated user's id injected as a request attribute
   * @return list of product response DTOs for the tenant
   */
  @Operation(summary = "List products for authenticated user", description = "Returns a paginated"
      + " list of products for the current user/tenant")
  @GetMapping
  public ResponseEntity<Page<ProductResponse>> getAll(
      @RequestAttribute("userId") String userId,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDir
  ) {
    Sort sort = Sort.by(Sort.Direction.valueOf(sortDir.toUpperCase()), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<ProductResponse> products = productService.getAllByUserId(userId, pageable);

    return ResponseEntity.ok(products);
  }

  /**
   * Create a new product for the authenticated user (tenant).
   *
   * @param userId the authenticated user's id injected as a request attribute
   * @param dto    create request payload containing product fields
   * @return the created product as a response DTO
   */
  @Operation(summary = "Create product", description = "Create a new product for the "
      + "authenticated user")
  @PostMapping
  public ResponseEntity<URI> create(
      @RequestAttribute("userId") String userId,
      @Validated @RequestBody CreateProductRequest dto
  ) {
    ProductResponse created = productService.createProduct(userId, dto);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.id())
        .toUri();
    log.info("Generated location URI: {}", location);
    return ResponseEntity.created(location).body(location);
  }

  /**
   * Update an existing product belonging to the authenticated user.
   *
   * @param userId    the authenticated user's id injected as a request attribute
   * @param productId the id of the product to update (string form of String)
   * @param dto       update request payload with optional fields to modify
   * @return the updated product as a response DTO
   */
  @Operation(summary = "Update product", description = "Update a product owned by the "
      + "authenticated user")
  @PutMapping(path = "/{id}")
  public ResponseEntity<ProductResponse> update(
      @RequestAttribute("userId") String userId,
      @PathVariable("id") Long productId,
      @RequestBody UpdateProductRequest dto
  ) {
    ProductResponse updated = productService.updateProduct(userId, productId, dto);
    return ResponseEntity.ok(updated);
  }

  /**
   * Delete a product owned by the authenticated user.
   *
   * @param userId    the authenticated user's id injected as a request attribute
   * @param productId the id of the product to delete (string form of String)
   * @return 204 No Content on success
   */
  @Operation(summary = "Delete product", description = "Delete a product owned by the "
      + "authenticated user")
  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> delete(
      @RequestAttribute("userId") String userId,
      @PathVariable("id") Long productId
  ) {
    productService.deleteProduct(userId, productId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Get a product by id for the authenticated user (tenant).
   *
   * @param userId    the authenticated user's id injected as a request attribute
   * @param productId the id of the product to retrieve
   * @return the product as an encrypted JSON string
   */
  @Operation(summary = "Get product by id", description = "Get single product by id for the "
      + "authenticated user (response is encrypted)")
  @GetMapping(path = "/{id}")
  public ResponseEntity<String> getById(
      @RequestAttribute("userId") String userId,
      @PathVariable("id") Long productId
  ) {
    Product product = productService.getProductEntityByIdAndUserId(productId, userId);
    ProductResponse responseDto = getProductResponse(product);
    try {
      String encryptedResponse = encryptionUtils.encryptJson(responseDto);
      return ResponseEntity.ok(encryptedResponse);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static ProductResponse getProductResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getSku(),
        product.getCategory(),
        product.getPrice(),
        product.getDescription(),
        product.getFeatures()
    );
  }

  /**
   * Decrypt an encrypted product JSON string.
   *
   * @param encryptedMessage the encrypted JSON string representing a product
   * @return the decrypted product as a response DTO
   */
  @Operation(summary = "Decrypt product JSON", description = "Decrypt an encrypted product JSON "
      + "string and return DTO")
  @PostMapping(path = "/decrypt")
  public ResponseEntity<ProductResponse> decryptMessage(
      @RequestBody String encryptedMessage
  ) {
    try {
      ProductResponse responseDto =
          encryptionUtils.decryptJson(encryptedMessage, ProductResponse.class);
      return ResponseEntity.ok(responseDto);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
