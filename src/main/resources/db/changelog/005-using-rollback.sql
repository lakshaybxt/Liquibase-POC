--liquibase formatted sql
--changeset arsh:005
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult: 0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='users' AND COLUMN_NAME='language';
--precondition-column-exists tableName:users columnName:language

ALTER TABLE users ADD COLUMN language VARCHAR(10)
DEFAULT 'JAVA' NOT NULL;

--rollback ALTER TABLE users DROP COLUMN language;
