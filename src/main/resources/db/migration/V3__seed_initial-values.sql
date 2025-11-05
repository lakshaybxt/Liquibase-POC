-- Seed the sequence with initial value
INSERT INTO users_seq (next_val)
SELECT 1
WHERE NOT EXISTS (SELECT * FROM users_seq);

-- Seed the sequence with initial value
INSERT INTO products_seq (next_val)
                                              SELECT 1
                                              WHERE NOT EXISTS (SELECT * FROM products_seq);