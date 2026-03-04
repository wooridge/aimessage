package com.aimessage.service;

import com.aimessage.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class NewsClassifierService {
    
    // 分类关键词映射
    private static final Map<String, String[]> CATEGORY_KEYWORDS = Map.of(
        "model", new String[]{"模型", "model", "GPT", "Gemini", "Claude", "Llama", "发布", "更新", "版本"},
        "agent", new String[]{"agent", "智能体", "autonomous", "代理", "助手", "assistant"},
        "research", new String[]{"论文", "paper", "arxiv", "研究", "research", "学术", "突破"},
        "funding", new String[]{"融资", "funding", "投资", "investment", "估值", "独角兽", "million", "billion"},
        "policy", new String[]{"政策", "监管", "regulation", "安全", "safety", "governance", "伦理", "法律"},
        "chip", new String[]{"芯片", "chip", "GPU", "TPU", "NPU", "算力", "hardware", "硬件"},
        "product", new String[]{"产品", "product", "应用", "app", "工具", "tool", "平台", "platform"},
        "china", new String[]{"中国", "国产", "百度", "阿里", "腾讯", "字节", "智谱", "月之暗面", "minimax"}
    );
    
    // 重要性评分关键词
    private static final Map<String, Integer> IMPORTANCE_KEYWORDS = Map.ofEntries(
        Map.entry("发布", 3),
        Map.entry("推出", 2),
        Map.entry("突破", 3),
        Map.entry("重大", 3),
        Map.entry("首次", 2),
        Map.entry("开源", 2),
        Map.entry("融资", 2),
        Map.entry(" billion", 3),
        Map.entry("GPT-5", 4),
        Map.entry("GPT-4", 3),
        Map.entry("Gemini", 2),
        Map.entry("Claude", 2),
        Map.entry("OpenAI", 2),
        Map.entry("Google", 2),
        Map.entry("DeepMind", 2),
        Map.entry("Anthropic", 2),
        Map.entry("Meta", 2),
        Map.entry("论文", 1),
        Map.entry("arxiv", 1),
        Map.entry("Nature", 3),
        Map.entry("Science", 3),
        Map.entry("菲尔兹奖", 4),
        Map.entry("诺贝尔奖", 4)
    );
    
    // 信源权重
    private static final Map<String, Integer> SOURCE_WEIGHTS = Map.ofEntries(
        Map.entry("OpenAI Blog", 10),
        Map.entry("Google AI Blog", 10),
        Map.entry("DeepMind Blog", 10),
        Map.entry("Anthropic Blog", 9),
        Map.entry("ArXiv AI", 8),
        Map.entry("ArXiv CL", 8),
        Map.entry("ArXiv LG", 8),
        Map.entry("TechCrunch AI", 7),
        Map.entry("The Verge AI", 7),
        Map.entry("量子位", 7),
        Map.entry("InfoQ AI", 6),
        Map.entry("Hacker News", 6),
        Map.entry("GitHub Trending", 5)
    );
    
    public Category classify(String title, String content, List<Category> categories) {
        String text = (title + " " + content).toLowerCase();
        
        // 默认分类
        Category defaultCategory = categories.stream()
                .filter(c -> c.getName().equals("model"))
                .findFirst()
                .orElse(categories.get(0));
        
        int maxScore = 0;
        Category bestCategory = defaultCategory;
        
        for (Category category : categories) {
            String catName = category.getName();
            if (CATEGORY_KEYWORDS.containsKey(catName)) {
                int score = 0;
                for (String keyword : CATEGORY_KEYWORDS.get(catName)) {
                    if (text.contains(keyword.toLowerCase())) {
                        score++;
                    }
                }
                if (score > maxScore) {
                    maxScore = score;
                    bestCategory = category;
                }
            }
        }
        
        return bestCategory;
    }
    
    public int scoreImportance(String title, String content, String sourceName) {
        String text = (title + " " + content).toLowerCase();
        int score = 5; // 基础分
        
        // 关键词加分
        for (Map.Entry<String, Integer> entry : IMPORTANCE_KEYWORDS.entrySet()) {
            if (text.contains(entry.getKey().toLowerCase())) {
                score += entry.getValue();
            }
        }
        
        // 信源权重
        score += SOURCE_WEIGHTS.getOrDefault(sourceName, 0);
        
        // 标题特征
        if (title.contains("发布") || title.contains("推出") || title.contains("发布")) {
            score += 1;
        }
        
        // 限制在1-10之间
        return Math.max(1, Math.min(10, score));
    }
    
    public String detectCategory(String text) {
        String lower = text.toLowerCase();
        
        if (lower.contains("融资") || lower.contains("商业") || lower.contains("funding") || lower.contains("investment")) {
            return "funding";
        }
        if (lower.contains("安全") || lower.contains("政策") || lower.contains("监管") || lower.contains("safety")) {
            return "policy";
        }
        if (lower.contains("模型") || lower.contains("产品") || lower.contains("更新") || lower.contains("model")) {
            return "model";
        }
        if (lower.contains("agent") || lower.contains("智能体")) {
            return "agent";
        }
        if (lower.contains("中国") || lower.contains("国产")) {
            return "china";
        }
        if (lower.contains("芯片") || lower.contains("算力") || lower.contains("chip")) {
            return "chip";
        }
        if (lower.contains("机器人") || lower.contains("具身")) {
            return "robot";
        }
        if (lower.contains("学术") || lower.contains("研究") || lower.contains("paper")) {
            return "research";
        }
        return "model";
    }
}
