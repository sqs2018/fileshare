create user resourcefile;
create table download_log
(
    id          int auto_increment
        primary key,
    user_name   varchar(50) null,
    create_time datetime    null,
    file_id     int         null
);

create table file
(
    id          int auto_increment
        primary key,
    file_name   varchar(100)  not null,
    parent_id   int default 0 not null,
    type        varchar(50)   not null,
    resource_id int           null,
    create_time datetime      not null,
    update_time datetime      not null,
    owner       varchar(50)   null
)
    charset = utf8mb3;

create table resource
(
    id          int auto_increment
        primary key,
    size        int default 0 not null,
    create_time datetime      not null,
    update_time datetime      not null,
    path        varchar(100)  not null,
    identifier  varchar(100)  null
)
    charset = utf8mb3;

create table resource_chunk
(
    id                 bigint auto_increment
        primary key,
    chunk_number       int          not null,
    chunk_size         int          not null,
    current_chunk_size int          not null,
    total_size         int          not null,
    identifier         varchar(100) not null,
    filename           varchar(100) not null,
    relative_path      varchar(300) not null,
    total_chunks       int          not null,
    create_time        datetime     not null,
    update_time        datetime     not null,
    md5                varchar(32)  null,
    constraint unique_flag
        unique (identifier, chunk_number)
)
    charset = utf8mb3;

create table user
(
    id          int auto_increment
        primary key,
    user_name   varchar(50)   not null,
    password    varchar(50)   not null,
    type        int           not null,
    create_time datetime      not null,
    login_time  datetime      null,
    login_ip    varchar(100)  not null,
    flag        int default 0 null,
    constraint user_pk
        unique (user_name)
);

INSERT INTO user (id, user_name, password, type, create_time, login_time, login_ip, flag) VALUES (2, 'admin', 'c4ca4238a0b923820dcc509a6f75849b', 1, '2024-09-21 16:06:52', '2024-11-05 22:31:10', '127.0.0.1', 0);
INSERT INTO user (id, user_name, password, type, create_time, login_time, login_ip, flag) VALUES (1366, '5', 'c4ca4238a0b923820dcc509a6f75849b', 0, '2024-10-22 01:27:39', '2024-11-06 08:26:59', '192.168.116.150', 0);
INSERT INTO user (id, user_name, password, type, create_time, login_time, login_ip, flag) VALUES (1367, '6', 'c4ca4238a0b923820dcc509a6f75849b', 0, '2024-10-22 01:27:39', '2024-10-26 22:11:34', '127.0.0.1', 0);
INSERT INTO user (id, user_name, password, type, create_time, login_time, login_ip, flag) VALUES (1567, '7', 'c4ca4238a0b923820dcc509a6f75849b', 0, '2024-11-05 20:04:38', null, '127.0.0.1', 0);
INSERT INTO user (id, user_name, password, type, create_time, login_time, login_ip, flag) VALUES (1569, '8', 'c4ca4238a0b923820dcc509a6f75849b', 0, '2024-11-05 22:31:48', '2024-11-05 23:32:06', '127.0.0.1', 0);
