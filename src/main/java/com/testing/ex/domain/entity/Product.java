package com.testing.ex.domain.entity;

import com.testing.ex.domain.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Product entity is generic: can represent phones, tablets, etc.
 * Multi-tenancy is enforced by tenantId column and repository filters.
 * Faster lookups + no duplicate data for the same tenant.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "ix_products_tenant_sku", columnList = "tenantId,sku", unique = true),
        @Index(name = "ix_products_tenant_category", columnList = "tenantId,category"),
        @Index(name = "ix_products_tenant_name", columnList = "tenantId,name")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "sku", nullable = false, length = 80)
    private String sku;

    @Column(name = "category", nullable = false, length = 60)
    private String category;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "description", length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "features")
    @MapKeyColumn(name = "feature_key")
    private Map<String, String> features;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback to set timestamps before persisting.
     */
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback to update the updatedAt timestamp before updating.
     */
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}


