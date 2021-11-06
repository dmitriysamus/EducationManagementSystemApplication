DELETE FROM groups_users;
DELETE FROM user_roles;
DELETE FROM users_groups;
--DELETE FROM admins;
DELETE FROM groups;
--DELETE FROM teachers;
DELETE FROM roles;
DELETE FROM tokens;
DELETE FROM users;

INSERT INTO groups(group_num) VALUES
(999);

INSERT INTO roles(id, name) VALUES
(1, 'ROLE_ADMIN'), (2, 'ROLE_TEACHER'), (3, 'ROLE_USER');

INSERT INTO users(id, username, email, password) VALUES
(1, 'admin', 'admin@admin.com', '$2a$10$EfVS7r4YFJVUKtoKtipoAuuj.e6z7ed/nEDNGrXB2z6M52d9zmtkW'),
(2, 'teacher', 'teacher@teacher.com', '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS'),
(3, 'user', 'user@user.com', '$2a$10$7JGsM41kbXX7/vJ2lc3pb.wdoIoANWTme.NErCU2TSv1RcPnDaBaS');

INSERT INTO user_roles VALUES
(1, 1), (1, 2), (1, 3), (2, 2), (2, 3), (3, 3);
--alter sequence users_id_seq restart with 10;
