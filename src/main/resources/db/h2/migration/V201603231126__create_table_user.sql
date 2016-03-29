CREATE SEQUENCE SEQ_USERS START WITH 1 INCREMENT BY 1 NOCYCLE;

CREATE TABLE USERS (
    id BIGINT DEFAULT SEQ_USERS.nextval,
    username varchar(255) not null,
    password varchar(255) not null,
    role varchar(50) not null
);
