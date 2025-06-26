package com.xkw.bcl.ai.framework.provider.deepseek;

import com.fasterxml.jackson.databind.JsonNode;
import com.xkw.bcl.ai.framework.config.BclAiFrameworkProperties;
import com.xkw.bcl.ai.framework.core.AiMessage;
import com.xkw.bcl.ai.framework.core.BaseAiServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * DeepSeek AI 服务实现
 * 支持DeepSeek系列模型的API调用
 *
 * @author chowsama
 * @date 2025/06/26
 */
@Slf4j
public class DeepSeekService extends BaseAiServiceImpl {

    private static final String DEFAULT_API_URL = "https://api.deepseek.com";
    private static final String CHAT_ENDPOINT = "/chat/completions";

    public DeepSeekService(BclAiFrameworkProperties.ProviderConfig config, int timeout, int readTimeout) {
        super(config, timeout, readTimeout);

        // 如果没有配置API URL，使用默认值
        if (config.getApiUrl() == null || config.getApiUrl().trim().isEmpty()) {
            config.setApiUrl(DEFAULT_API_URL);
        }

        log.info("DeepSeek Service 初始化完成，API URL: {}, 模型: {}",
                config.getApiUrl(), config.getDefaultModel());
    }

    @Override
    public String chat(List<AiMessage> messages) {
        try {
            Map<String, Object> requestBody = buildRequestBody(messages, false);
            String responseBody = sendPost(requestBody);
            return parseResponse(responseBody);
        } catch (Exception e) {
            log.error("DeepSeek 聊天请求失败", e);
            throw new RuntimeException("DeepSeek API 调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void chatStream(List<AiMessage> messages, Consumer<String> callback) {
        try {
            Map<String, Object> requestBody = buildRequestBody(messages, true);
            sendPostStream(requestBody, callback);
        } catch (Exception e) {
            log.error("DeepSeek 流式聊天请求失败", e);
            callback.accept("{\"error\": \"DeepSeek 流式API调用失败: " + e.getMessage() + "\"}");
        }
    }

    @Override
    public String getProviderName() {
        return "deepseek";
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
            log.warn("DeepSeek 服务不可用: {}", e.getMessage());
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
            // DeepSeek 支持的参数（兼容OpenAI格式）
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
            // DeepSeek 特有参数
            if (parameters.containsKey("logprobs")) {
                requestBody.put("logprobs", parameters.get("logprobs"));
            }
            if (parameters.containsKey("top_logprobs")) {
                requestBody.put("top_logprobs", parameters.get("top_logprobs"));
            }
            // JSON输出模式
            if (parameters.containsKey("response_format")) {
                requestBody.put("response_format", parameters.get("response_format"));
            }
        }

        // 设置默认值
        requestBody.putIfAbsent("temperature", 1.0); // DeepSeek默认为1.0
        requestBody.putIfAbsent("max_tokens", 4096);  // DeepSeek默认为4096

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
                throw new RuntimeException("DeepSeek API 错误 [" + errorType + "]: " + errorMessage);
            }

            // 解析正常响应
            if (jsonNode.has("choices") && jsonNode.get("choices").isArray() && jsonNode.get("choices").size() > 0) {
                JsonNode choice = jsonNode.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    String content = choice.get("message").get("content").asText();

                    // 对于 deepseek-reasoner 模型，可能有推理内容
                    if (choice.get("message").has("reasoning_content")) {
                        String reasoningContent = choice.get("message").get("reasoning_content").asText();
                        if (reasoningContent != null && !reasoningContent.trim().isEmpty()) {
                            log.debug("DeepSeek 推理过程: {}", reasoningContent);
                            // 可以选择是否将推理过程包含在最终响应中
                            // return "推理过程:\n" + reasoningContent + "\n\n最终答案:\n" + content;
                        }
                    }

                    return content;
                }
            }

            throw new RuntimeException("无法解析 DeepSeek 响应: " + responseBody);
        } catch (Exception e) {
            log.error("解析 DeepSeek 响应失败: {}", responseBody, e);
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
                    if (choice.has("delta")) {
                        JsonNode delta = choice.get("delta");

                        // 处理普通内容
                        if (delta.has("content")) {
                            String content = delta.get("content").asText();
                            if (!content.isEmpty()) {
                                callback.accept(content);
                            }
                        }

                        // 处理推理内容（deepseek-reasoner模型特有）
                        if (delta.has("reasoning_content")) {
                            String reasoningContent = delta.get("reasoning_content").asText();
                            if (!reasoningContent.isEmpty()) {
                                log.debug("DeepSeek 推理流: {}", reasoningContent);
                                // 可以选择是否将推理过程发送给回调
                                // callback.accept("[推理] " + reasoningContent);
                            }
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
        return model != null && !model.trim().isEmpty() ? model : "deepseek-chat";
    }

    /**
     * 转换消息格式为 DeepSeek API 所需格式
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