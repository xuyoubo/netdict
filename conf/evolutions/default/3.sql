# words schema

# --- !Ups
CREATE SEQUENCE comment_id_seq;
CREATE TABLE comment (
    id integer not null DEFAULT nextval('word_id_seq'),
    word_id integer,
    member_id integer,
    content varchar(1000),
    PRIMARY KEY (id)
);
ALTER SEQUENCE comment_id_seq OWNED BY comment.id;

# --- !Downs
DROP TABLE comment;
