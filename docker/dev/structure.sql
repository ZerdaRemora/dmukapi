CREATE SEQUENCE public.comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.comment
(
    id bigint NOT NULL DEFAULT nextval('comment_id_seq'::regclass),
    author character varying COLLATE pg_catalog."default",
    body character varying COLLATE pg_catalog."default",
    comment_source character varying COLLATE pg_catalog."default",
    date timestamp with time zone,
    score integer,
    local_site_id character varying COLLATE pg_catalog."default",
    article_title character varying(255) COLLATE pg_catalog."default",
    created_date timestamp with time zone,
    CONSTRAINT comment_pkey PRIMARY KEY (id)
);
