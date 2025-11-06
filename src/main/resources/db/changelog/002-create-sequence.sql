--liquibase formatted sql

--changeset arsh:002
--preconditions onFail:MARK_RAN onError:HALT
--precondition-not tableExists tableName=users_seq
--precondition-not tableExists tableName=products_seq

CREATE TABLE users_seq (
                           next_val BIGINT NOT NULL
);

CREATE TABLE products_seq (
                              next_val BIGINT NOT NULL
);



--rollback DROP TABLE products_seq;
--rollback DROP TABLE users_seq;