-- Generate a view for products with selected fields
CREATE OR REPLACE VIEW product_viewsAjay AS
SELECT
    id AS product_id,
    tenant_id,
    name,
    category,
    price
FROM products;