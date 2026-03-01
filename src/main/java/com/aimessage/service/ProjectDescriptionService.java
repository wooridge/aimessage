package com.aimessage.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProjectDescriptionService {

    private static final Map<String, String> FUNCTION_KEYWORDS = new HashMap<>();

    static {
        // 包管理/依赖
        FUNCTION_KEYWORDS.put("package manager", "Python包管理工具，用于安装和管理依赖");
        FUNCTION_KEYWORDS.put("package", "包管理工具，用于依赖管理");
        FUNCTION_KEYWORDS.put("dependency", "依赖管理工具");
        FUNCTION_KEYWORDS.put("venv", "虚拟环境管理工具");
        
        // 终端/编辑器
        FUNCTION_KEYWORDS.put("terminal", "终端模拟器，用于命令行操作");
        FUNCTION_KEYWORDS.put("terminal emulator", "终端模拟器，提供命令行界面");
        FUNCTION_KEYWORDS.put("code editor", "代码编辑器，用于编写代码");
        FUNCTION_KEYWORDS.put("editor", "文本编辑器，用于代码编写");
        FUNCTION_KEYWORDS.put("ide", "集成开发环境");
        
        // AI/编码助手
        FUNCTION_KEYWORDS.put("coding agent", "AI编码助手，辅助编程开发");
        FUNCTION_KEYWORDS.put("ai coding", "AI编程助手");
        FUNCTION_KEYWORDS.put("copilot", "AI编程助手");
        FUNCTION_KEYWORDS.put("code completion", "代码补全工具");
        
        // 安全/加密
        FUNCTION_KEYWORDS.put("zero-knowledge", "零知识证明协议，用于隐私保护");
        FUNCTION_KEYWORDS.put("cryptography", "加密工具，用于数据安全");
        FUNCTION_KEYWORDS.put("encryption", "加密工具");
        FUNCTION_KEYWORDS.put("privacy", "隐私保护工具");
        FUNCTION_KEYWORDS.put("security", "安全工具");
        
        // 运行时/语言
        FUNCTION_KEYWORDS.put("runtime", "运行时环境，用于执行代码");
        FUNCTION_KEYWORDS.put("javascript runtime", "JavaScript运行时环境");
        FUNCTION_KEYWORDS.put("programming language", "编程语言");
        
        // 远程桌面
        FUNCTION_KEYWORDS.put("remote desktop", "远程桌面应用，用于远程控制");
        FUNCTION_KEYWORDS.put("remote control", "远程控制工具");
        
        // 学习/教程
        FUNCTION_KEYWORDS.put("exercises", "编程练习平台，用于学习编程");
        FUNCTION_KEYWORDS.put("learning", "学习工具");
        FUNCTION_KEYWORDS.put("tutorial", "教程平台");
        
        // LLM/AI模型
        FUNCTION_KEYWORDS.put("llm", "大语言模型工具");
        FUNCTION_KEYWORDS.put("large language model", "大语言模型平台");
        FUNCTION_KEYWORDS.put("ai model", "AI模型部署工具");
        FUNCTION_KEYWORDS.put("ollama", "本地大模型运行工具");
        
        // 框架集合
        FUNCTION_KEYWORDS.put("awesome", "开发资源集合，汇总优质框架和工具");
        FUNCTION_KEYWORDS.put("curated list", "精选资源列表");
        
        // 文件同步
        FUNCTION_KEYWORDS.put("file synchronization", "文件同步工具，用于数据备份");
        FUNCTION_KEYWORDS.put("sync", "同步工具");
        FUNCTION_KEYWORDS.put("backup", "备份工具");
        
        // Git工具
        FUNCTION_KEYWORDS.put("git", "Git工具，用于版本控制");
        FUNCTION_KEYWORDS.put("git commands", "Git命令行工具");
        FUNCTION_KEYWORDS.put("git ui", "Git图形界面工具");
        
        // 搜索/查找
        FUNCTION_KEYWORDS.put("fuzzy finder", "模糊搜索工具，快速查找文件");
        FUNCTION_KEYWORDS.put("search", "搜索工具");
        FUNCTION_KEYWORDS.put("finder", "文件查找工具");
        
        // 静态网站生成器
        FUNCTION_KEYWORDS.put("static site", "静态网站生成器");
        FUNCTION_KEYWORDS.put("static site generator", "静态网站构建工具");
        FUNCTION_KEYWORDS.put("website builder", "网站构建工具");
        
        // 容器/K8s
        FUNCTION_KEYWORDS.put("container", "容器编排平台");
        FUNCTION_KEYWORDS.put("container orchestration", "容器编排系统");
        FUNCTION_KEYWORDS.put("kubernetes", "K8s容器管理平台");
        FUNCTION_KEYWORDS.put("k8s", "Kubernetes容器管理");
        
        // Web框架
        FUNCTION_KEYWORDS.put("web framework", "Web开发框架");
        FUNCTION_KEYWORDS.put("http framework", "HTTP服务框架");
        FUNCTION_KEYWORDS.put("api framework", "API开发框架");
        FUNCTION_KEYWORDS.put("rest api", "REST API框架");
        
        // 代理/网络
        FUNCTION_KEYWORDS.put("reverse proxy", "反向代理服务器");
        FUNCTION_KEYWORDS.put("proxy", "代理服务器");
        FUNCTION_KEYWORDS.put("tunnel", "网络隧道工具");
        FUNCTION_KEYWORDS.put("nat", "内网穿透工具");
        
        // 跨平台桌面应用
        FUNCTION_KEYWORDS.put("desktop application", "跨平台桌面应用框架");
        FUNCTION_KEYWORDS.put("cross-platform", "跨平台开发框架");
        FUNCTION_KEYWORDS.put("gui framework", "图形界面框架");
        
        // 监控/日志
        FUNCTION_KEYWORDS.put("monitor", "系统监控工具");
        FUNCTION_KEYWORDS.put("monitoring", "监控观测平台");
        FUNCTION_KEYWORDS.put("logging", "日志管理工具");
        FUNCTION_KEYWORDS.put("observability", "可观测性平台");
        
        // 数据库
        FUNCTION_KEYWORDS.put("database", "数据库系统");
        FUNCTION_KEYWORDS.put("sql", "SQL数据库");
        FUNCTION_KEYWORDS.put("nosql", "NoSQL数据库");
        FUNCTION_KEYWORDS.put("cache", "缓存数据库");
        
        // 消息队列
        FUNCTION_KEYWORDS.put("message queue", "消息队列系统");
        FUNCTION_KEYWORDS.put("mq", "消息中间件");
        FUNCTION_KEYWORDS.put("streaming", "流处理平台");
        
        // 构建工具
        FUNCTION_KEYWORDS.put("build tool", "项目构建工具");
        FUNCTION_KEYWORDS.put("compiler", "编译器工具");
        FUNCTION_KEYWORDS.put("bundler", "代码打包工具");
        
        // 测试工具
        FUNCTION_KEYWORDS.put("testing", "测试框架");
        FUNCTION_KEYWORDS.put("test framework", "自动化测试工具");
        
        // CI/CD
        FUNCTION_KEYWORDS.put("ci/cd", "持续集成/部署工具");
        FUNCTION_KEYWORDS.put("pipeline", "流水线工具");
        FUNCTION_KEYWORDS.put("automation", "自动化工具");
        
        // 文档
        FUNCTION_KEYWORDS.put("documentation", "文档生成工具");
        FUNCTION_KEYWORDS.put("docs", "文档平台");
        
        // 爬虫/数据
        FUNCTION_KEYWORDS.put("crawler", "网络爬虫工具");
        FUNCTION_KEYWORDS.put("scraper", "数据抓取工具");
        FUNCTION_KEYWORDS.put("data processing", "数据处理工具");
    }

    public String generateChineseDescription(String name, String description, String language) {
        if (description == null || description.isEmpty()) {
            return generateDefaultDescription(name, language);
        }

        String lowerDesc = description.toLowerCase();
        String lowerName = name.toLowerCase();

        // 1. 首先尝试从描述中提取具体功能（最精确）
        String extractedFunction = extractFunctionFromDescription(lowerDesc);
        if (extractedFunction != null) {
            return extractedFunction;
        }

        // 2. 尝试匹配关键词映射表
        for (Map.Entry<String, String> entry : FUNCTION_KEYWORDS.entrySet()) {
            String keyword = entry.getKey();
            if (lowerDesc.contains(keyword)) {
                return entry.getValue();
            }
        }

        // 3. 如果描述中没有匹配，尝试匹配名称
        for (Map.Entry<String, String> entry : FUNCTION_KEYWORDS.entrySet()) {
            String keyword = entry.getKey();
            if (lowerName.contains(keyword)) {
                return entry.getValue();
            }
        }

        // 4. 返回默认描述
        return generateDefaultDescription(name, language);
    }

    private String extractFunctionFromDescription(String desc) {
        // 按优先级顺序匹配，越具体的匹配越靠前
        
        // 1. 包管理相关（最具体）
        if (desc.contains("package manager") || desc.contains("package and project manager")) {
            return "Python包管理工具，用于安装和管理依赖";
        }
        
        // 2. AI编码助手
        if (desc.contains("coding agent") || desc.contains("coding agent that runs in your terminal")) {
            return "AI编码助手，在终端中辅助编程开发";
        }
        
        // 3. 终端模拟器
        if (desc.contains("terminal emulator") || (desc.contains("terminal") && desc.contains("cross-platform"))) {
            return "终端模拟器，提供跨平台命令行界面";
        }
        
        // 4. 代码编辑器
        if (desc.contains("high-performance") && desc.contains("multiplayer") && desc.contains("code editor")) {
            return "高性能多人协作代码编辑器";
        }
        if (desc.contains("code editor") || desc.contains("text editor")) {
            return "代码编辑器，用于编写和编辑代码";
        }
        
        // 5. 运行时环境
        if (desc.contains("runtime for javascript") || desc.contains("runtime for javascript and typescript")) {
            return "JavaScript/TypeScript运行时环境，用于执行JS代码";
        }
        
        // 6. 远程桌面
        if (desc.contains("remote desktop application") || (desc.contains("remote desktop") && desc.contains("open-source"))) {
            return "开源远程桌面应用，用于远程控制计算机";
        }
        
        // 7. 跨平台桌面应用框架
        if (desc.contains("desktop and mobile applications") && desc.contains("web frontend")) {
            return "跨平台桌面应用框架，使用Web技术构建原生应用";
        }
        
        // 8. 编程练习
        if (desc.contains("exercises") && desc.contains("reading and writing")) {
            return "编程练习平台，用于学习Rust语言";
        }
        
        // 9. 本地LLM运行工具
        if (desc.contains("llm") || desc.contains("get up and running with") && desc.contains("llama")) {
            return "本地大语言模型运行工具，支持多种AI模型";
        }
        
        // 10. 文件同步
        if (desc.contains("file synchronization") || (desc.contains("continuous file synchronization"))) {
            return "文件同步工具，用于持续数据备份";
        }
        
        // 11. Git工具
        if (desc.contains("terminal ui for git") || (desc.contains("git") && desc.contains("terminal ui"))) {
            return "Git终端界面工具，简化版本控制操作";
        }
        
        // 12. 模糊搜索
        if (desc.contains("fuzzy finder") || (desc.contains("command-line fuzzy finder"))) {
            return "模糊搜索工具，快速查找文件和内容";
        }
        
        // 13. 静态网站生成器
        if (desc.contains("static site generator") || (desc.contains("static site") && desc.contains("generator"))) {
            return "静态网站生成器，用于构建高性能网站";
        }
        if (desc.contains("fastest framework") && desc.contains("building websites")) {
            return "高速网站构建框架";
        }
        
        // 14. 容器编排
        if (desc.contains("container scheduling") || (desc.contains("kubernetes") && desc.contains("container"))) {
            return "容器编排平台，用于管理和调度容器化应用";
        }
        
        // 15. Web框架
        if (desc.contains("http web framework") || (desc.contains("web framework") && desc.contains("http"))) {
            return "HTTP Web框架，用于构建高性能网络服务";
        }
        
        // 16. 反向代理
        if (desc.contains("reverse proxy") || (desc.contains("expose a local server") && desc.contains("internet"))) {
            return "反向代理工具，用于内网穿透和负载均衡";
        }
        
        // 17. 安全/零知识
        if (desc.contains("zero-knowledge") || desc.contains("trust-minimized")) {
            return "零知识证明协议，用于保护数据隐私和跨链桥接";
        }
        
        // 18. 监控
        if (desc.contains("monitor") || desc.contains("observability")) {
            return "系统监控工具，用于性能观测";
        }
        
        // 19. 编程语言
        if (desc.contains("programming language") && desc.contains("empowering")) {
            return "Rust编程语言，用于构建可靠高效的软件";
        }
        
        // 20. 通用匹配
        if (desc.contains("package") && desc.contains("manager")) {
            return "包管理工具，用于安装和管理依赖";
        }
        if (desc.contains("install") && desc.contains("package")) {
            return "包安装工具，用于管理项目依赖";
        }
        if (desc.contains("command line") || desc.contains("cli")) {
            return "命令行工具，用于终端操作";
        }
        if (desc.contains("web") && desc.contains("framework")) {
            return "Web开发框架，用于构建网络应用";
        }
        if (desc.contains("desktop") && desc.contains("application")) {
            return "桌面应用程序";
        }
        
        return null;
    }

    private String generateDefaultDescription(String name, String language) {
        String langStr = language != null && !language.isEmpty() ? language : "开源";
        
        // 根据名称推测类型
        String lowerName = name.toLowerCase();
        if (lowerName.contains("go") || lowerName.contains("gin")) {
            return "Go语言开发工具或框架";
        }
        if (lowerName.contains("rust")) {
            return "Rust语言相关工具";
        }
        if (lowerName.contains("js") || lowerName.contains("node")) {
            return "JavaScript/Node.js工具";
        }
        if (lowerName.contains("py") || lowerName.contains("python")) {
            return "Python开发工具";
        }
        
        return langStr + "开发的实用工具";
    }
}
