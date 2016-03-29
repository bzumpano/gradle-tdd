CREATE SEQUENCE SEQ_USERS;

CREATE TABLE USERS (
    id BIGINT PRIMARY KEY DEFAULT NEXTVAL('SEQ_USERS'),
    username varchar(255) not null,
    password varchar(255) not null,
    role varchar(50) not null
);
