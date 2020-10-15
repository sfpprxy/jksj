create table remote_work
(
    id             text        not null PRIMARY KEY,
    user_id        text        not null,
    link           text        not null,
    time_start     timestamptz not null,
    time_end       timestamptz,
    time_applied   int,
    time_remaining text
);

create table biz_cache
(
    id        text not null PRIMARY KEY,
    cache_str text not null
);
