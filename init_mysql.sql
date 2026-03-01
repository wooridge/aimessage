CREATE DATABASE IF NOT EXISTS aimessage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'aimessage'@'localhost' IDENTIFIED BY 'Ren280919@';
GRANT ALL PRIVILEGES ON aimessage.* TO 'aimessage'@'localhost';
FLUSH PRIVILEGES;
