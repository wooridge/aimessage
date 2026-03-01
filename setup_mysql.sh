#!/bin/bash
systemctl stop mysql
rm -rf /var/lib/mysql/*
mysqld --initialize-insecure --user=mysql
systemctl start mysql
sleep 3
mysql -e "CREATE DATABASE aimessage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "CREATE USER 'aimessage'@'%' IDENTIFIED BY 'Ren280919@';"
mysql -e "GRANT ALL PRIVILEGES ON aimessage.* TO 'aimessage'@'%';"
mysql -e "FLUSH PRIVILEGES;"
echo "MySQL setup completed"
