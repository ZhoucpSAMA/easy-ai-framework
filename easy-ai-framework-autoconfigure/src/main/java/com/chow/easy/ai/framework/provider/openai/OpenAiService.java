package com.chow.easy.ai.framework.provider.openai;

import com.chow.easy.ai.framework.config.BclAiFrameworkProperties;
import com.chow.easy.ai.framework.core.AiMessage;
import com.chow.easy.ai.framework.core.BaseAiServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * OpenAI AI 服务实现
 * 支持GPT系列模型的API调用
 *
 * @author chowsama
 * @date 2025/06/26
 */
@Slf4j
public class OpenAiService extends BaseAiServiceImpl {

    private static final String DEFAULT_API_URL = "https://api.openai.com/v1";
    private static final String CHAT_ENDPOINT = "/chat/completions";

    public OpenAiService(BclAiFrameworkProperties.ProviderConfig config, int timeout, int readTimeout) {
        super(config, timeout, readTimeout);

        // 如果没有配置API URL，使用默认值
        if (config.getApiUrl() == null || config.getApiUrl().trim().isEmpty()) {
            config.setApiUrl(DEFAULT_API_URL);
        }

        log.info("OpenAI Service 初始化完成，API URL: {}, 模型: {}",
                config.getApiUrl(), config.getDefaultModel());
    }

    @Override
    public String chat(List<AiMessage> messages) {
        try {
            Map<String, Object> requestBody = buildRequestBody(messages, false);
            String responseBody = sendPost(requestBody);
            return parseResponse(responseBody);
        } catch (Exception e) {
            log.error("OpenAI 聊天请求失败", e);
            throw new RuntimeException("OpenAI API 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void chatStream(List<AiMessage> messages, Consumer<String> callback) {
        try {
            Map<String, Object> requestBody = buildRequestBody(messages, true);
            sendPostStream(requestBody, callback);
        } catch (Exception e) {
            log.error("OpenAI 流式聊天请求失败", e);
            callback.accept("{\"error\": \"OpenAI 流式API调用失败: " + e.getMessage() + "\"}");
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

    @Override
    public boolean isAvailable() {
        try {
            // 简单的可用性检查：发送测试消息
            List<AiMessage> testMessages = new ArrayList<>();
            testMessages.add(new AiMessage("user", "Hello"));
            String response = chat(testMessages);
            return response != null && !response.trim().isEmpty();
        } catch (Exception e) {
            log.warn("OpenAI 服务不可用: {}", e.getMessage());
            return false;
        }
    }

    @Override
    protected Map<String, Object> buildRequestBody(List<AiMessage> messages, boolean stream) {
        Map<String, Object> requestBody = new HashMap<>();

        // 基本参数
        requestBody.put("model", getModelName());
        requestBody.put("messages", convertMessages(messages));
        requestBody.put("stream", stream);

        // 从配置中获取自定义参数
        Map<String, Object> parameters = config.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            // OpenAI 常用参数映射
            if (parameters.containsKey("temperature")) {
                requestBody.put("temperature", parameters.get("temperature"));
            }
            if (parameters.containsKey("max_tokens")) {
                requestBody.put("max_tokens", parameters.get("max_tokens"));
            }
            if (parameters.containsKey("top_p")) {
                requestBody.put("top_p", parameters.get("top_p"));
            }
            if (parameters.containsKey("frequency_penalty")) {
                requestBody.put("frequency_penalty", parameters.get("frequency_penalty"));
            }
            if (parameters.containsKey("presence_penalty")) {
                requestBody.put("presence_penalty", parameters.get("presence_penalty"));
            }
            if (parameters.containsKey("stop")) {
                requestBody.put("stop", parameters.get("stop"));
            }
            if (parameters.containsKey("n")) {
                requestBody.put("n", parameters.get("n"));
            }
            if (parameters.containsKey("logit_bias")) {
                requestBody.put("logit_bias", parameters.get("logit_bias"));
            }
            if (parameters.containsKey("user")) {
                requestBody.put("user", parameters.get("user"));
            }
        }

        // 设置默认值
        requestBody.putIfAbsent("temperature", 0.7);
        requestBody.putIfAbsent("max_tokens", 2000);

        return requestBody;
    }

    @Override
    protected String parseResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 检查是否有错误
            if (jsonNode.has("error")) {
                JsonNode error = jsonNode.get("error");
                String errorMessage = error.has("message") ? error.get("message").asText() : "Unknown error";
                String errorType = error.has("type") ? error.get("type").asText() : "unknown_error";
                throw new RuntimeException("OpenAI API 错误 [" + errorType + "]: " + errorMessage);
            }

            // 解析正常响应
            if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) {
                JsonNode choice = jsonNode.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
            }

            throw new RuntimeException("无法解析 OpenAI 响应: " + responseBody);
        } catch (Exception e) {
            log.error("解析 OpenAI 响应失败: {}", responseBody, e);
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }

    @Override
    protected void parseStreamResponse(String line, Consumer<String> callback) {
        try {
            if (line.trim().isEmpty() || line.equals("data: [DONE]")) {
                return;
            }

            // 处理 SSE 格式
            if (line.startsWith("data: ")) {
                String jsonString = line.substring(6).trim();
                if (jsonString.equals("[DONE]")) {
                    return;
                }

                JsonNode jsonNode = objectMapper.readTree(jsonString);

                // 检查错误
                if (jsonNode.has("error")) {
                    JsonNode error = jsonNode.get("error");
                    String errorMessage = error.has("message") ? error.get("message").asText() : "Unknown error";
                    callback.accept("{\"error\": \"" + errorMessage + "\"}");
                    return;
                }

                // 解析流式内容
                if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) {
                    JsonNode choice = jsonNode.get("choices").get(0);
                    if (choice.has("delta") && choice.get("delta").has("content")) {
                        String content = choice.get("delta").get("content").asText();
                        if (!content.isEmpty()) {
                            callback.accept(content);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析流式响应失败: {}", line, e);
            // 继续处理下一行，不中断整个流
        }
    }

    @Override
    protected String getApiEndpoint() {
        return CHAT_ENDPOINT;
    }

    /**
     * 获取模型名称
     */
    private String getModelName() {
        String model = config.getDefaultModel();
        return model != null && !model.trim().isEmpty() ? model : "gpt-3.5-turbo";
    }

    /**
     * 转换消息格式为 OpenAI API 所需格式
     */
    private List<Map<String, String>> convertMessages(List<AiMessage> messages) {
        return messages.stream()
                .map(msg -> {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", msg.getRole());
                    message.put("content", msg.getContent());
                    return message;
                })
                .collect(Collectors.toList());
    }
} 