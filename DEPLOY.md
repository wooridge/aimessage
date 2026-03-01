# AI Daily 部署指南

## 项目简介

AI Daily 是一个AI资讯聚合平台，自动抓取和展示AI相关新闻、GitHub热门项目等内容。

**在线访问**: http://wangyurenpractice.xyz

## 技术栈

- **后端**: Spring Boot 3.x + Java 17
- **数据库**: MySQL 8.0
- **前端**: Thymeleaf + Tailwind CSS
- **部署**: Nginx 反向代理

## 服务器环境要求

- Ubuntu 20.04+ / CentOS 7+
- OpenJDK 17
- MySQL 8.0
- Nginx (宝塔面板已安装)

## 部署步骤

### 1. 准备服务器

购买阿里云/腾讯云服务器，配置:
- 最低配置: 2核2G
- 开放端口: 22(SSH), 80(HTTP), 443(HTTPS), 8080(应用)

### 2. 安装Java

```bash
apt update
apt install -y openjdk-17-jdk
java -version
```

### 3. 安装并配置MySQL

```bash
apt install -y mysql-server

# 初始化MySQL
mysqld --initialize-insecure --user=mysql
systemctl start mysql

# 登录MySQL创建数据库
mysql -h 127.0.0.1 -P 3306 -u root

# 执行SQL
CREATE DATABASE aimessage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'aimessage'@'%' IDENTIFIED BY '你的密码';
GRANT ALL PRIVILEGES ON aimessage.* TO 'aimessage'@'%';
FLUSH PRIVILEGES;
EXIT;
```

### 4. 部署应用

```bash
# 创建应用目录
mkdir -p /opt/aimessage/logs

# 上传jar包到 /opt/aimessage/aimessage-1.0.0.jar

# 创建配置文件
cat > /opt/aimessage/application-prod.yml << 'EOF'
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/aimessage?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: aimessage
    password: 你的密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

server:
  port: 8080

logging:
  level:
    root: INFO
    com.aimessage: INFO
  file:
    name: /opt/aimessage/logs/aimessage.log
EOF

# 启动应用
cd /opt/aimessage
nohup java -jar -Dspring.profiles.active=prod aimessage-1.0.0.jar > /opt/aimessage/logs/nohup.out 2>&1 &
```

### 5. 配置Nginx反向代理

编辑宝塔面板的Nginx配置：

```bash
cat > /www/server/panel/vhost/nginx/aimessage.conf << 'EOF'
server {
    listen 80 default_server;
    server_name wangyurenpractice.xyz www.wangyurenpractice.xyz;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

# 修改phpfpm_status.conf，避免端口冲突
sed -i 's/listen 80;/listen 127.0.0.1:80;/' /www/server/panel/vhost/nginx/phpfpm_status.conf

# 重载Nginx
/www/server/nginx/sbin/nginx -t
/www/server/nginx/sbin/nginx -s reload
```

### 6. 配置域名DNS

在域名服务商控制台添加A记录：
- 主机记录: @ 和 www
- 记录类型: A
- 记录值: 你的服务器IP
- TTL: 默认

### 7. 开放防火墙

```bash
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 8080/tcp
```

## 管理命令

### 查看应用状态
```bash
ps aux | grep java | grep -v grep
tail -f /opt/aimessage/logs/nohup.out
```

### 停止应用
```bash
pkill -f aimessage
```

### 重启应用
```bash
pkill -f aimessage
sleep 2
cd /opt/aimessage
nohup java -jar -Dspring.profiles.active=prod aimessage-1.0.0.jar > /opt/aimessage/logs/nohup.out 2>&1 &
```

### 一键重启脚本

创建 `/opt/aimessage/restart.sh`:

```bash
#!/bin/bash
echo "停止旧进程..."
pkill -f aimessage
sleep 2

echo "启动应用..."
cd /opt/aimessage
nohup java -jar -Dspring.profiles.active=prod aimessage-1.0.0.jar > /opt/aimessage/logs/nohup.out 2>&1 &

echo "等待启动..."
sleep 5

echo "检查状态..."
ps aux | grep java | grep -v grep
curl -s http://127.0.0.1:8080/ | head -1
echo "重启完成！"
```

赋予执行权限：
```bash
chmod +x /opt/aimessage/restart.sh
```

## 数据同步

### 同步GitHub热门项目
```bash
curl -X POST http://localhost:8080/api/sync/github
```

### 重新生成中文描述
```bash
curl -X POST http://localhost:8080/api/sync/github/regenerate-descriptions
```

## 常见问题

### 1. 502 Bad Gateway
- 检查Java应用是否运行: `ps aux | grep java`
- 检查Nginx配置: `/www/server/nginx/sbin/nginx -t`
- 检查端口监听: `netstat -tlnp | grep 8080`

### 2. 数据库连接失败
- 检查MySQL是否运行: `systemctl status mysql`
- 检查数据库用户权限
- 检查配置文件密码

### 3. 域名无法访问
- 检查DNS解析: `nslookup wangyurenpractice.xyz`
- 检查防火墙: `ufw status`
- 检查安全组规则（阿里云/腾讯云控制台）

## 更新部署

1. 停止旧应用
2. 备份数据库: `mysqldump -u root -p aimessage > backup.sql`
3. 上传新jar包
4. 启动应用
5. 验证: `curl http://localhost:8080/`

## 联系方式

如有问题，请提交GitHub Issue。
