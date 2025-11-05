-- Indexes
CREATE INDEX ix_products_tenant_category ON products (tenant_id, category);

CREATE INDEX ix_products_tenant_name ON products (tenant_id, name);

CREATE INDEX ix_products_tenant_sku ON products (tenant_id, sku);