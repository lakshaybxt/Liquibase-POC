--liquibase formatted sql

--changeset arsh:003
--preconditions onFail:MARK_RAN onError:HALT

INSERT INTO users_seq (next_val)
SELECT 1
    WHERE NOT EXISTS (SELECT * FROM users_seq);

INSERT INTO products_seq (next_val)
SELECT 1
    WHERE NOT EXISTS (SELECT * FROM products_seq);

--rollback DELETE FROM users_seq;
--rollback DELETE FROM products_seq;
