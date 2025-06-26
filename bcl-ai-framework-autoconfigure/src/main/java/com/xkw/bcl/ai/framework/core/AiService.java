package com.xkw.bcl.ai.framework.core;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author chowsama
 * @date 2025/06/26 09:30
 */
public interface AiService {
    /**
     * 简单对话
     */
    String chat(String prompt);

    /**
     * 多轮对话
     */
    String chat(List<AiMessage> messages);

    /**
     * 流式对话
     */
    void chatStream(String prompt, Consumer<String> callback);

    /**
     * 流式多轮对话
     */
    void chatStream(List<AiMessage> messages, Consumer<String> callback);

    /**
     * 获取提供商名称
     */
    String getProviderName();

    /**
     * 检查服务可用性
     */
    boolean isAvailable();
}
