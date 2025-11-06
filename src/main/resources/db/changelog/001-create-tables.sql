--liquibase formatted sql

--changeset arsh:001
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name IN ('products', 'users', 'product_features');

-- USERS TABLE
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN DEFAULT FALSE,
                       verification_code VARCHAR(255),
                       verification_expiration DATETIME,
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL
);

-- PRODUCTS TABLE
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          tenant_id VARCHAR(36) NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          sku VARCHAR(50) NOT NULL,
                          category VARCHAR(60) NOT NULL,
                          price DECIMAL(12,2) NOT NULL,
                          description VARCHAR(2000),
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL,
                          UNIQUE KEY uq_products_tenant_sku (tenant_id, sku)
);

CREATE UNIQUE INDEX ix_products_tenant_sku ON products (tenant_id, sku);
CREATE INDEX ix_products_tenant_category ON products (tenant_id, category);
CREATE INDEX ix_products_tenant_name ON products (tenant_id, name);

-- PRODUCT FEATURES TABLE
CREATE TABLE product_features (
                                  product_id BIGINT NOT NULL,
                                  feature_key VARCHAR(255) NOT NULL,
                                  features VARCHAR(255),
                                  PRIMARY KEY (product_id, feature_key),
                                  CONSTRAINT fk_product_features_product FOREIGN KEY (product_id)
                                      REFERENCES products (id)
                                      ON DELETE CASCADE
);

--rollback DROP TABLE product_features;
--rollback DROP TABLE products;
--rollback DROP TABLE users;