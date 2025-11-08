package com.testing.ex.service.impl;

import com.testing.ex.domain.dto.request.CreateProductRequest;
import com.testing.ex.domain.dto.request.UpdateProductRequest;
import com.testing.ex.domain.dto.response.ProductResponse;
import com.testing.ex.domain.entity.Product;
import com.testing.ex.repos.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Test")
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private Product testProduct;
    private CreateProductRequest testCreateRequest;
    private UpdateProductRequest testUpdateRequest;
    private ProductResponse testProductResponse;

    @BeforeEach
    void init() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .tenantId(null)
                .description("This is a test product")
                .sku("SKU12345")
                .price(BigDecimal.valueOf(1L))
                .category("Test Category")
                .features(null)
                .build();

        testCreateRequest = CreateProductRequest.builder()

                .name("New Product")
                .description("Description of new product")
                .sku("NEWSKU123")
                .price(BigDecimal.valueOf(10L))
                .category("New Category")
                .features(null)
                .build();

        testUpdateRequest = UpdateProductRequest.builder()
                .name("Updated Product")
                .description("Updated description")
                .sku("UPDSKU123")
                .price(BigDecimal.valueOf(20L))
                .category("Updated Category")
                .features(null)
                .build();

        testProductResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .sku("SKU12345")
                .price(BigDecimal.valueOf(1L))
                .category("Test Category")
                .features(null)
                .description("This is test product")
                .build();
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully when valid request is provided")
        void testCreateProduct_Success() {
            // Given
            final String userId = "user-123";

            /**
             * so like if there is something happening inside while calling a method lke heere i am creating
             * like calling any other mehthod inside createProduct then we need to mock that method call here
             * using Mockito.when().thenReturn() syntax
             */
            Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class)))
                    .thenReturn(testProduct);


            // Expected response
            testProductResponse = ProductResponse.builder()
                    .id(testProduct.getId())
                    .name(testProduct.getName())
                    .description(testProduct.getDescription())
                    .sku(testProduct.getSku())
                    .price(testProduct.getPrice())
                    .category(testProduct.getCategory())
                    .features(testProduct.getFeatures())
                    .build();

            Mockito.doReturn(testProductResponse)
                    .when(productServiceImpl)
                    .toResponse(testProduct);


            // When
            final ProductResponse result = productServiceImpl.createProduct(userId, testCreateRequest);

            // Then
            assertNotNull(result);
            assertEquals(testProductResponse.id(), result.id());
            assertEquals(testProductResponse.name(), result.name());
            assertEquals(testProductResponse.sku(), result.sku());
            assertEquals(testProductResponse.price(), result.price());
            assertEquals(testProductResponse.category(), result.category());

            Mockito.verify(productRepository, Mockito.times(1))
                    .save(ArgumentMatchers.any(Product.class));

        }

        @Test
        @DisplayName("Should throw exception when product creation fails")
        void testCreateProduct_Failure_ThrowException() {
            // Given
            final String userId = "user-123";

            Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            final RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                productServiceImpl.createProduct(userId, testCreateRequest);
            });

            assertEquals("Database error", ex.getMessage());

            Mockito.verify(productRepository, Mockito.times(1))
                    .save(ArgumentMatchers.any(Product.class));
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully when valid request is provided")
        void testUpdateProduct_Successfully() {
            // Given
            final String userId = "user-123";
            final Long productId = 1L;

            Mockito.when(productRepository.findByIdAndTenantId(productId, userId))
                    .thenReturn(Optional.of(testProduct));

            Mockito.when(productRepository.save(ArgumentMatchers.any()))
                    .thenReturn(testProduct);

            // When
            ProductResponse response = productServiceImpl.updateProduct(userId, productId, testUpdateRequest);
            // Then
            Mockito.verify(productRepository, Mockito.times(1))
                    .findByIdAndTenantId(productId, userId);
            Mockito.verify(productRepository, Mockito.times(1))
                    .save(ArgumentMatchers.any(Product.class));

            assertNotNull(response);
            assertEquals(testUpdateRequest.name(), response.name());
            assertEquals(testUpdateRequest.price(), response.price());
            assertEquals(testUpdateRequest.category(), response.category());


        }

        @Test
        @DisplayName("Should throw exception when product to update is not found")
        void testUpdateProduct_Failure_ThrowException() {
            // Given
            final String userId = "user-123";
            final Long productId = 1L;

            Mockito.when(productRepository.findByIdAndTenantId(productId, userId))
                    .thenReturn(Optional.empty());

            // When & Then
            final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                productServiceImpl.updateProduct(userId, productId, testUpdateRequest);
            });

            assertEquals("Product not found or access denied", ex.getMessage());
            Mockito.verify(productRepository, Mockito.times(1))
                    .findByIdAndTenantId(productId, userId);
            Mockito.verify(ProductServiceImplTest.this.productRepository, Mockito.never())
                    .save(ArgumentMatchers.any(Product.class));
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {}

    @Nested
    @DisplayName("Find Product by ID Tests")
    class FindProductByIdTests {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProduct() {
            // Given
            final String userId = "user-123";
            final Long productId = 1L;

            // When
            Mockito.when(productRepository.findByIdAndTenantId(productId, userId))
                    .thenReturn(Optional.of(testProduct));

            final Product result = productServiceImpl.getProductEntityByIdAndUserId(productId, userId);

            // Then
            assertNotNull(result);
            assertEquals(testProduct, result);
            Mockito.verify(productRepository, Mockito.times(1))
                    .findByIdAndTenantId(productId, userId);
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Given
            final String userId = "user-123";
            final Long productId = 1L;

            // When & Then
            Mockito.when(productRepository.findByIdAndTenantId(productId, userId))
                    .thenReturn(Optional.empty());

            final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
                productServiceImpl.getProductEntityByIdAndUserId(productId, userId);
            });

            assertEquals("Product not found or access denied", ex.getMessage());
            Mockito.verify(productRepository, Mockito.times(1))
                    .findByIdAndTenantId(productId, userId);
        }


        @Test
        @DisplayName("Should throw exception when product id is null")
        void shouldHandleNullId() {
            // Given
            final String userId = "user-123";

            // When & Then
            Mockito.when(productRepository.findByIdAndTenantId(null, userId))
                    .thenReturn(Optional.empty());

            final IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> {
                productServiceImpl.getProductEntityByIdAndUserId(null, userId);
            });

            assertEquals("Product not found or access denied", ex.getMessage());
            Mockito.verify(productRepository, Mockito.times(1))
                    .findByIdAndTenantId(null, userId);
        }
    }
}
