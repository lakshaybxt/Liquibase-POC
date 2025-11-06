--liquibase formatted sql
--changeset arsh:004
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='role';

ALTER TABLE users ADD COLUMN role VARCHAR(50)
DEFAULT 'USER' NOT NULL;

--rollback ALTER TABLE users DROP COLUMN role;
