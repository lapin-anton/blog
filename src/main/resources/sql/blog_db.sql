create database blog;

create table post (
  id bigserial primary key,
  title varchar(500),
  text text,
  tags text,
  likes_count integer
);

create table comment (
  id bigserial primary key,
  post_id bigint references post(id),
  text text
);