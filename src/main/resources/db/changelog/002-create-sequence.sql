--liquibase formatted sql

--changeset arsh:002
--preconditions onFail:MARK_RAN onError:HALT
--preconditions-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_name IN ('users_seq', 'products_seq');

CREATE TABLE users_seq (
                           next_val BIGINT NOT NULL
);

CREATE TABLE products_seq (
                              next_val BIGINT NOT NULL
);



--rollback DROP TABLE products_seq;
--rollback DROP TABLE users_seq;