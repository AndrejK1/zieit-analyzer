set search_path = 'analyzer';
create schema analyzer;

create schema shop_1;

create table analyzer.shop_info
(
    id      bigint primary key,
    shop_id bigint not null
);

create table analyzer.analytics_reports
(
    id           bigint primary key,
    shop_id      bigint not null,
    report       jsonb,
    period_start timestamptz,
    period_end   timestamptz
);

create table shop_1.product
(
    id         text not null primary key,
    title      text not null,
    url        text
);

create table shop_1.customer
(
    id         text not null primary key,
    first_name text not null,
    last_name  text,
    email      text not null
);

create table shop_1.purchase_item
(
    id          text   not null primary key,
    order_id    text   not null,
    customer_id text   not null,
    product_id  text   not null,
    quantity    bigint not null,
    price       numeric(10, 2),
    purchase_timestamp   timestamptz
);
