-- Create users table
CREATE TABLE if NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(255),
    verification_expiration TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
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

-- Features map table (for @ElementCollection)
CREATE TABLE IF NOT EXISTS product_features (
    product_id BIGINT NOT NULL,
    feature_key VARCHAR(255) NOT NULL,
    features VARCHAR(255),
    PRIMARY KEY (product_id, feature_key),
    CONSTRAINT fk_product_features_product FOREIGN KEY (product_id)
    REFERENCES products(id) ON DELETE CASCADE
);