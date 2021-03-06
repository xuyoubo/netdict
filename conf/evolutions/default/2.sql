# words schema

# --- !Ups
CREATE SEQUENCE member_id_seq;
CREATE TABLE member (
    id integer not null DEFAULT nextval('word_id_seq'),
    name varchar(30) NOT NULL,
    password varchar(30),
    PRIMARY KEY (id)
);

ALTER SEQUENCE member_id_seq OWNED BY member.id;

insert into member (name,password) values ('test','123456')
# --- !Downs
DROP TABLE member;
