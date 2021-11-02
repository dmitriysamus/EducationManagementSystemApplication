--create table admins (id  serial not null,
--                     user_creation_date timestamp,
--                     email varchar(255),
--                     last_visited_date timestamp,
--                     password varchar(255),
--                     username varchar(255), primary key (id));
create table groups (groupNum int4 not null,
                     teacher_id int4, primary key (groupNum));
create table groups_users (group_id int4 not null,
                           users_id int4 not null,
                           primary key (group_id, users_id));
create table users_groups (user_id int4 not null,
                           groups_id int4 not null,
                           primary key (user_id, groups_id));
create table roles (id  serial not null,
                    name varchar(20),
                    primary key (id));
--create table teachers (id  serial not null,
--                       user_creation_date timestamp,
--                       email varchar(255),
--                       last_visited_date timestamp,
--                       password varchar(255),
--                       username varchar(255), primary key (id));
--create table teachers_groups (teacher_id int4 not null,
--                              groups_id int4 not null, primary key (teacher_id, groups_id));
create table tokens (id  serial not null,
                     token_status boolean,
                     token_creation_date timestamp,
                     token_expiry_date timestamp,
                     token varchar(500) not null,
                     token_user_id int4, primary key (id));
create table user_roles (user_id int4 not null,
                         role_id int4 not null, primary key (user_id, role_id));
create table users (id  serial not null,
                    user_creation_date timestamp,
                    email varchar(255),
                    last_visited_date timestamp,
                    password varchar(255),
                    username varchar(255),
                    group_id int4, primary key (id));