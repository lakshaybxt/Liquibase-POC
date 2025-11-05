-- Indexes
CREATE TABLE IF NOT EXISTS productsTest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    category VARCHAR(60) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    description VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_products_tenant_sku UNIQUE (tenant_id, sku)
);
