CREATE SEQUENCE SEQ_SURVEYS;

CREATE TABLE SURVEYS (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('SEQ_SURVEYS'),
    description varchar(255) not null
);