package com.aimessage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    private static final Logger log = LoggerFactory.getLogger(TranslationService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // 翻译缓存
    private final Map<String, String> translationCache = new HashMap<>();

    // MyMemory API (免费翻译API，每小时1000次请求限制)
    private static final String TRANSLATE_API_URL = "https://api.mymemory.translated.net/get";

    public TranslationService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 翻译文本为中文
     */
    public String translateToChinese(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 检查缓存
        if (translationCache.containsKey(text)) {
            return translationCache.get(text);
        }

        // 如果已经是中文，直接返回
        if (isChinese(text)) {
            return text;
        }

        // 尝试调用API翻译
        String translated = callTranslateAPI(text, "en", "zh");

        // 如果API翻译失败，使用简单翻译
        if (translated.equals(text)) {
            translated = simpleTranslate(text);
        }

        // 缓存结果
        translationCache.put(text, translated);

        return translated;
    }

    /**
     * 调用翻译API
     */
    private String callTranslateAPI(String text, String sourceLang, String targetLang) {
        try {
            log.debug("Translating text: {}", text.substring(0, Math.min(text.length(), 50)));

            // MyMemory API使用GET请求
            String langPair = sourceLang + "|" + targetLang;
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            String response = webClient.get()
                    .uri(TRANSLATE_API_URL + "?q=" + encodedText + "&langpair=" + langPair)
                    .retrieve()
                    .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Translation API error: {}", errorBody);
                                    return reactor.core.publisher.Mono.error(
                                        new RuntimeException("Translation API Error: " + errorBody)
                                    );
                                })
                    )
                    .bodyToMono(String.class)
                    .block();

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.has("responseData")) {
                    JsonNode responseData = jsonNode.get("responseData");
                    if (responseData.has("translatedText")) {
                        String translated = responseData.get("translatedText").asText();
                        log.debug("Translation successful: {}", translated.substring(0, Math.min(translated.length(), 50)));
                        return translated;
                    }
                }
            }
        } catch (WebClientResponseException e) {
            log.error("Translation API HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Translation API error: {}", e.getMessage());
        }

        // API调用失败，返回原文
        return text;
    }

    /**
     * 简单的规则翻译（作为备用）
     */
    private String simpleTranslate(String text) {
        String lower = text.toLowerCase();

        // 常见AI术语翻译映射
        Map<String, String> translations = new HashMap<>();
        translations.put("artificial intelligence", "人工智能");
        translations.put("machine learning", "机器学习");
        translations.put("deep learning", "深度学习");
        translations.put("neural network", "神经网络");
        translations.put("large language model", "大语言模型");
        translations.put("llm", "大语言模型");
        translations.put("generative ai", "生成式AI");
        translations.put("chatbot", "聊天机器人");
        translations.put("algorithm", "算法");
        translations.put("model", "模型");
        translations.put("training", "训练");
        translations.put("inference", "推理");
        translations.put("dataset", "数据集");
        translations.put("open source", "开源");
        translations.put("startup", "初创公司");
        translations.put("funding", "融资");
        translations.put("investment", "投资");
        translations.put("billion", "十亿");
        translations.put("million", "百万");
        translations.put("announced", "发布");
        translations.put("launched", "推出");
        translations.put("released", "发布");
        translations.put("introduced", "引入");
        translations.put("developed", "开发");
        translations.put("research", "研究");
        translations.put("paper", "论文");
        translations.put("study", "研究");
        translations.put("framework", "框架");
        translations.put("platform", "平台");
        translations.put("tool", "工具");
        translations.put("api", "API");
        translations.put("cloud", "云");
        translations.put("computing", "计算");
        translations.put("chip", "芯片");
        translations.put("hardware", "硬件");
        translations.put("software", "软件");
        translations.put("application", "应用");

        // 尝试替换关键词
        String result = text;
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            result = result.replaceAll("(?i)\\b" + entry.getKey() + "\\b", entry.getValue());
        }

        return result;
    }

    /**
     * 检查文本是否为中文（公共方法）
     */
    public boolean isChinese(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        // 检查是否包含中文字符
        return text.chars().anyMatch(c -> c >= 0x4E00 && c <= 0x9FA5);
    }

    /**
     * 转义JSON字符串
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
