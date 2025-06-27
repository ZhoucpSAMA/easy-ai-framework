package com.chow.easy.ai.framework.util;

import com.chow.easy.ai.framework.core.AiMessage;
import com.chow.easy.ai.framework.core.AiServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author chowsama
 * @date 2025/06/26 10:51
 */
@Component
public class AiServiceUtil {
    private final AiServiceFactory aiServiceFactory;

    @Autowired
    public AiServiceUtil(AiServiceFactory aiServiceFactory) {
        this.aiServiceFactory = aiServiceFactory;
    }

    /**
     * 使用默认提供商进行对话
     */
    public String chat(String prompt) {
        return aiServiceFactory.getDefaultService().chat(prompt);
    }

    /**
     * 使用指定提供商进行对话
     */
    public String chat(String providerName, String prompt) {
        return aiServiceFactory.getService(providerName).chat(prompt);
    }

    /**
     * 多轮对话
     */
    public String chat(String providerName, List<AiMessage> messages) {
        return aiServiceFactory.getService(providerName).chat(messages);
    }

    /**
     * 流式对话
     */
    public void chatStream(String prompt, Consumer<String> callback) {
        aiServiceFactory.getDefaultService().chatStream(prompt, callback);
    }

    /**
     * 指定提供商的流式对话
     */
    public void chatStream(String providerName, String prompt, Consumer<String> callback) {
        aiServiceFactory.getService(providerName).chatStream(prompt, callback);
    }
}
