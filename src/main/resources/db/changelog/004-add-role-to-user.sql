--liquibase formatted sql
--changeset arsh:004
--preconditions onFail:MARK_RAN onError:HALT
--not
--  columnExists tableName="users" columnName="role"

ALTER TABLE users ADD COLUMN role VARCHAR(50)
DEFAULT 'USER' NOT NULL;
