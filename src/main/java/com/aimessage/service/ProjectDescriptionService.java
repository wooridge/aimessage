package com.aimessage.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProjectDescriptionService {

    private static final Map<String, String> KEYWORD_DESCRIPTIONS = new HashMap<>();

    static {
        // AI/ML 相关
        KEYWORD_DESCRIPTIONS.put("ai", "人工智能项目，用于构建智能应用");
        KEYWORD_DESCRIPTIONS.put("machine learning", "机器学习项目，用于数据分析和预测");
        KEYWORD_DESCRIPTIONS.put("deep learning", "深度学习项目，用于神经网络训练");
        KEYWORD_DESCRIPTIONS.put("neural network", "神经网络项目，用于模拟人脑计算");
        KEYWORD_DESCRIPTIONS.put("llm", "大语言模型项目，用于自然语言处理");
        KEYWORD_DESCRIPTIONS.put("gpt", "GPT模型相关项目，用于文本生成");
        KEYWORD_DESCRIPTIONS.put("chatbot", "聊天机器人项目，用于对话交互");
        KEYWORD_DESCRIPTIONS.put("nlp", "自然语言处理项目，用于文本分析");
        KEYWORD_DESCRIPTIONS.put("computer vision", "计算机视觉项目，用于图像识别");
        KEYWORD_DESCRIPTIONS.put("image", "图像处理项目，用于视觉分析");
        KEYWORD_DESCRIPTIONS.put("video", "视频处理项目，用于媒体分析");
        KEYWORD_DESCRIPTIONS.put("speech", "语音识别项目，用于音频处理");
        KEYWORD_DESCRIPTIONS.put("voice", "语音处理项目，用于声音合成");

        // 开发工具
        KEYWORD_DESCRIPTIONS.put("framework", "开发框架，用于构建应用程序");
        KEYWORD_DESCRIPTIONS.put("library", "工具库，提供常用功能封装");
        KEYWORD_DESCRIPTIONS.put("tool", "开发工具，提高开发效率");
        KEYWORD_DESCRIPTIONS.put("cli", "命令行工具，用于终端操作");
        KEYWORD_DESCRIPTIONS.put("ide", "集成开发环境，用于代码编写");
        KEYWORD_DESCRIPTIONS.put("editor", "代码编辑器，用于文本编辑");
        KEYWORD_DESCRIPTIONS.put("debugger", "调试工具，用于程序排错");
        KEYWORD_DESCRIPTIONS.put("test", "测试工具，用于质量保证");
        KEYWORD_DESCRIPTIONS.put("build", "构建工具，用于项目编译");
        KEYWORD_DESCRIPTIONS.put("deploy", "部署工具，用于发布应用");

        // 数据相关
        KEYWORD_DESCRIPTIONS.put("database", "数据库项目，用于数据存储");
        KEYWORD_DESCRIPTIONS.put("sql", "SQL工具，用于数据库操作");
        KEYWORD_DESCRIPTIONS.put("nosql", "NoSQL数据库，用于非结构化数据");
        KEYWORD_DESCRIPTIONS.put("cache", "缓存系统，用于提高性能");
        KEYWORD_DESCRIPTIONS.put("search", "搜索引擎，用于信息检索");
        KEYWORD_DESCRIPTIONS.put("data", "数据处理项目，用于数据分析");
        KEYWORD_DESCRIPTIONS.put("big data", "大数据项目，用于海量数据处理");
        KEYWORD_DESCRIPTIONS.put("etl", "数据ETL工具，用于数据转换");

        // 网络/Web
        KEYWORD_DESCRIPTIONS.put("web", "Web开发项目，用于网站建设");
        KEYWORD_DESCRIPTIONS.put("http", "HTTP工具，用于网络通信");
        KEYWORD_DESCRIPTIONS.put("api", "API工具，用于接口开发");
        KEYWORD_DESCRIPTIONS.put("server", "服务器项目，用于后端服务");
        KEYWORD_DESCRIPTIONS.put("client", "客户端项目，用于用户交互");
        KEYWORD_DESCRIPTIONS.put("proxy", "代理工具，用于网络转发");
        KEYWORD_DESCRIPTIONS.put("load balancer", "负载均衡器，用于流量分发");
        KEYWORD_DESCRIPTIONS.put("gateway", "网关项目，用于请求路由");

        // 安全
        KEYWORD_DESCRIPTIONS.put("security", "安全工具，用于保护系统");
        KEYWORD_DESCRIPTIONS.put("crypto", "加密工具，用于数据安全");
        KEYWORD_DESCRIPTIONS.put("auth", "认证系统，用于身份验证");
        KEYWORD_DESCRIPTIONS.put("oauth", "OAuth工具，用于第三方登录");
        KEYWORD_DESCRIPTIONS.put("jwt", "JWT工具，用于令牌管理");
        KEYWORD_DESCRIPTIONS.put("firewall", "防火墙项目，用于网络防护");

        // 容器/云
        KEYWORD_DESCRIPTIONS.put("docker", "Docker工具，用于容器化部署");
        KEYWORD_DESCRIPTIONS.put("kubernetes", "K8s工具，用于容器编排");
        KEYWORD_DESCRIPTIONS.put("container", "容器项目，用于应用隔离");
        KEYWORD_DESCRIPTIONS.put("cloud", "云原生项目，用于云计算");
        KEYWORD_DESCRIPTIONS.put("serverless", "无服务器项目，用于函数计算");
        KEYWORD_DESCRIPTIONS.put("microservices", "微服务项目，用于分布式架构");

        // 前端
        KEYWORD_DESCRIPTIONS.put("react", "React项目，用于构建用户界面");
        KEYWORD_DESCRIPTIONS.put("vue", "Vue项目，用于前端开发");
        KEYWORD_DESCRIPTIONS.put("angular", "Angular项目，用于企业级应用");
        KEYWORD_DESCRIPTIONS.put("frontend", "前端项目，用于网页开发");
        KEYWORD_DESCRIPTIONS.put("ui", "UI组件库，用于界面设计");
        KEYWORD_DESCRIPTIONS.put("css", "CSS工具，用于样式设计");

        // 区块链
        KEYWORD_DESCRIPTIONS.put("blockchain", "区块链项目，用于去中心化应用");
        KEYWORD_DESCRIPTIONS.put("bitcoin", "比特币相关项目");
        KEYWORD_DESCRIPTIONS.put("ethereum", "以太坊相关项目");
        KEYWORD_DESCRIPTIONS.put("smart contract", "智能合约项目");
        KEYWORD_DESCRIPTIONS.put("web3", "Web3项目，用于去中心化网络");

        // 游戏
        KEYWORD_DESCRIPTIONS.put("game", "游戏开发项目");
        KEYWORD_DESCRIPTIONS.put("game engine", "游戏引擎，用于游戏开发");

        // 其他
        KEYWORD_DESCRIPTIONS.put("chat", "聊天应用，用于即时通讯");
        KEYWORD_DESCRIPTIONS.put("message", "消息系统，用于信息传递");
        KEYWORD_DESCRIPTIONS.put("email", "邮件系统，用于邮件处理");
        KEYWORD_DESCRIPTIONS.put("monitor", "监控系统，用于性能观测");
        KEYWORD_DESCRIPTIONS.put("log", "日志系统，用于记录追踪");
        KEYWORD_DESCRIPTIONS.put("scheduler", "调度系统，用于任务管理");
        KEYWORD_DESCRIPTIONS.put("queue", "消息队列，用于异步处理");
        KEYWORD_DESCRIPTIONS.put("workflow", "工作流引擎，用于流程管理");
    }

    public String generateChineseDescription(String name, String description, String language) {
        if (description == null || description.isEmpty()) {
            return generateDefaultDescription(name, language);
        }

        String lowerDesc = description.toLowerCase();
        String lowerName = name.toLowerCase();

        // 根据关键词匹配生成描述
        StringBuilder chineseDesc = new StringBuilder();

        // 检查描述中的关键词
        for (Map.Entry<String, String> entry : KEYWORD_DESCRIPTIONS.entrySet()) {
            if (lowerDesc.contains(entry.getKey()) || lowerName.contains(entry.getKey())) {
                if (chineseDesc.length() > 0) {
                    chineseDesc.append("，");
                }
                chineseDesc.append(entry.getValue());
            }
        }

        // 如果没有匹配到关键词，生成默认描述
        if (chineseDesc.length() == 0) {
            chineseDesc.append(generateDefaultDescription(name, language));
        }

        // 添加编程语言信息
        if (language != null && !language.isEmpty()) {
            chineseDesc.append("，使用 ").append(language).append(" 开发");
        }

        return chineseDesc.toString();
    }

    private String generateDefaultDescription(String name, String language) {
        String langStr = language != null ? language : "多种语言";
        return "开源项目，使用 " + langStr + " 开发，提供实用功能";
    }
}
