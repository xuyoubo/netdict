# words schema

# --- !Ups
CREATE SEQUENCE word_id_seq;
CREATE TABLE word (
    id integer not null DEFAULT nextval('word_id_seq'),
    keyword varchar(255) NOT NULL,
    trans varchar(1000),
    search_count integer,
    favour_count integer,
    PRIMARY KEY (id)
);
ALTER SEQUENCE word_id_seq OWNED BY word.id;
# --- !Downs
DROP TABLE word;
