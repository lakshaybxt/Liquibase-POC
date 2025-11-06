--liquibase formatted sql

--changeset arsh:003
--preconditions onFail:MARK_RAN onError:HALT
--precondition-table-exists tableName:users_seq
--precondition-table-exists tableName:products_seq
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users_seq;
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM products_seq;

INSERT INTO users_seq (next_val) VALUES (1);
INSERT INTO products_seq (next_val) VALUES (1);
--rollback DELETE FROM users_seq;
--rollback DELETE FROM products_seq;
