drop table if exists comment;
drop table if exists post;

create table post (
    id bigint primary key,
    title varchar(500),
    image text,
    text text,
    tags text,
    likes_count integer default 0
);

create table comment (
     id bigserial primary key,
     post_id bigint references post(id),
     text text
);