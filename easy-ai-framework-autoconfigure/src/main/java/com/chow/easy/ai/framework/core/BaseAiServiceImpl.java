package com.chow.easy.ai.framework.core;

import com.chow.easy.ai.framework.config.BclAiFrameworkProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 * @author chowsama
 * @date 2025/06/26 09:33
 */

@Slf4j
public abstract class BaseAiServiceImpl implements AiService {

    protected final BclAiFrameworkProperties.ProviderConfig config;
    protected final ObjectMapper objectMapper;
    protected final OkHttpClient httpClient;

    protected BaseAiServiceImpl(BclAiFrameworkProperties.ProviderConfig config, int timeout, int readTimeout) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 简单对话
     *
     * @param prompt
     */
    @Override
    public String chat(String prompt) {
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant"));
        messages.add(new AiMessage("user", prompt));
        return chat(messages);
    }

    /**
     * 多轮对话
     *
     * @param messages
     */
    @Override
    public abstract String chat(List<AiMessage> messages);

    /**
     * 流式对话
     *
     * @param prompt
     * @param callback
     */
    @Override
    public void chatStream(String prompt, Consumer<String> callback) {
        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("system", "You are a helpful assistant"));
        messages.add(new AiMessage("user", prompt));
        chatStream(messages, callback);
    }

    /**
     * 流式多轮对话
     *
     * @param messages
     * @param callback
     */
    @Override
    public abstract void chatStream(List<AiMessage> messages, Consumer<String> callback);

    /**
     * 获取提供商名称
     */
    @Override
    public abstract String getProviderName();

    /**
     * 检查服务可用性
     */
    @Override
    public boolean isAvailable() {
        return false;
    }

    /**
     * 构建请求体
     */
    protected abstract Map<String, Object> buildRequestBody(List<AiMessage> messages, boolean stream);

    /**
     * 解析响应
     */
    protected abstract String parseResponse(String responseBody);

    /**
     * 解析流式响应
     */
    protected abstract void parseStreamResponse(String line, Consumer<String> callback);

    /**
     * 获取API端点
     */
    protected abstract String getApiEndpoint();

    /**
     * 发送POST请求
     */
    protected String sendPost(Map<String, Object> requestBody) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(MediaType.get("application/json"), json);

            Request request = new Request.Builder()
                    .url(config.getApiUrl() + getApiEndpoint())
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Request failed: " + response.code() + " " + response.message());
                }
                return response.body().string();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to send request", e);
        }
    }

    /**
     * 发送流式POST请求
     */
    protected void sendPostStream(Map<String, Object> requestBody, Consumer<String> callback) {
        try {
            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(MediaType.get("application/json"), json);

            Request request = new Request.Builder()
                    .url(config.getApiUrl() + getApiEndpoint())
                    .addHeader("Authorization", "Bearer " + config.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "text/event-stream")
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    callback.accept("{\"error\": \"Request failed: " + response.code() + "\"}");
                    return;
                }

                BufferedReader reader = new BufferedReader(new StringReader(response.body().string()));
                String line;
                while ((line = reader.readLine()) != null) {
                    parseStreamResponse(line, callback);
                }
            }
        } catch (Exception e) {
            callback.accept("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
