# 🤖 AI Daily - 每日AI新闻聚合系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

AI Daily 是一个自动化的AI新闻聚合与同步系统，每天上午8点自动抓取AI领域最新动态，并以美观的Web界面展示。

## ✨ 特性

- 🕐 **定时同步**: 每天上午8点自动抓取AI新闻
- 📊 **智能分类**: 自动归类到8大类别（融资、政策、模型、Agent、中国AI、芯片、机器人、学术研究）
- 🔥 **热点推荐**: 自动识别重要性≥8分的热点新闻
- 🎨 **美观界面**: 使用Tailwind CSS构建的响应式Web界面
- 🗄️ **数据持久**: 支持H2（开发）和MySQL（生产）数据库
- 🔌 **REST API**: 提供完整的API接口

## 🚀 快速开始

### 环境要求

- Java 17+
- Maven 3.8+
- （可选）MySQL 8.0+

### 本地运行

```bash
# 克隆项目
git clone https://github.com/wooridge/aimessage.git
cd aimessage

# 编译运行
mvn clean spring-boot:run

# 或打包后运行
mvn clean package -DskipTests
java -jar target/aimessage-1.0.0.jar
```

访问 http://localhost:8080 查看应用

### 生产环境部署

```bash
# 使用生产配置文件
java -jar target/aimessage-1.0.0.jar --spring.profiles.active=prod

# 或设置环境变量
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export PORT=8080
java -jar target/aimessage-1.0.0.jar
```

## 📁 项目结构

```
aimessage/
├── src/main/java/com/aimessage/
│   ├── AiMessageApplication.java    # 应用入口
│   ├── config/                       # 配置类
│   ├── controller/                   # Web控制器
│   ├── dto/                          # 数据传输对象
│   ├── entity/                       # 实体类
│   ├── repository/                   # 数据访问层
│   ├── scheduler/                    # 定时任务
│   └── service/                      # 业务逻辑层
├── src/main/resources/
│   ├── templates/                    # Thymeleaf模板
│   ├── static/                       # 静态资源
│   ├── application.yml               # 配置文件
│   └── application-prod.yml          # 生产环境配置
└── pom.xml                           # Maven配置
```

## 🔌 API接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/` | GET | Web首页 |
| `/api/report` | GET | 获取日报数据 |
| `/api/news/category/{category}` | GET | 按分类获取新闻 |
| `/api/sync` | POST | 手动触发同步 |
| `/h2-console` | GET | H2数据库控制台（开发环境） |

## 📊 数据来源

本项目数据来源于 [InsightScope API](https://api.insightscope.org/report)，涵盖以下信源：

- **国际媒体**: The Verge, TechCrunch, Bloomberg, Ars Technica, Wired
- **国内媒体**: 36氪, 量子位, InfoQ, 虎嗅, 新浪财经
- **社交媒体**: Twitter/X
- **官方博客**: GitHub Blog, Google Blog

## 📝 新闻分类

| 分类 | 图标 | 描述 |
|------|------|------|
| AI融资与商业动态 | 💰 | 融资、收购、商业合作 |
| AI安全与政策监管 | 🔒 | 政策法规、安全研究 |
| 模型发布与产品更新 | 🚀 | 新模型、产品发布 |
| AI Agent与智能体 | 🤖 | AI Agent、智能助手 |
| 中国AI动态 | 🇨🇳 | 国产AI发展 |
| AI芯片与算力 | ⚡ | 芯片、算力基础设施 |
| 具身智能与机器人 | 🔧 | 机器人、具身智能 |
| AI学术研究 | 📚 | 学术论文、研究进展 |

## ⚙️ 配置说明

### 应用配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:aimessagedb  # H2内存数据库
  jpa:
    hibernate:
      ddl-auto: create-drop       # 自动创建表结构

server:
  port: 8080                      # 服务端口
```

### 定时任务

- `0 0 8 * * ?` - 每天上午8点同步
- `0 0 */6 * * ?` - 每6小时同步一次

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

本项目采用 MIT 许可证

---

Made with ❤️ by [wooridge](https://github.com/wooridge)
