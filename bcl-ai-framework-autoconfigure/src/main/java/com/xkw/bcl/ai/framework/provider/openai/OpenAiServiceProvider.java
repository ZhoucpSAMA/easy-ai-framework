package com.xkw.bcl.ai.framework.provider.openai;

import com.xkw.bcl.ai.framework.config.BclAiFrameworkProperties;
import com.xkw.bcl.ai.framework.core.AiService;
import com.xkw.bcl.ai.framework.spi.AiServiceProvider;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAI AI服务提供商SPI实现
 *
 * @author chowsama
 * @date 2025/06/26
 */
public class OpenAiServiceProvider implements AiServiceProvider {

    private static final String PROVIDER_NAME = "openai";

    private static final List<String> SUPPORTED_MODELS = Arrays.asList(
            // GPT-4 系列
            "gpt-4",
            "gpt-4-turbo",
            "gpt-4-turbo-preview",
            "gpt-4-1106-preview",
            "gpt-4-0125-preview",
            "gpt-4-vision-preview",

            // GPT-3.5 系列
            "gpt-3.5-turbo",
            "gpt-3.5-turbo-16k",
            "gpt-3.5-turbo-1106",
            "gpt-3.5-turbo-0125",

            // 较新的模型
            "gpt-4o",
            "gpt-4o-mini",
            "chatgpt-4o-latest",

            // O1 系列推理模型
            "o1-preview",
            "o1-mini"
    );

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean supports(String providerName) {
        return PROVIDER_NAME.equalsIgnoreCase(providerName);
    }

    @Override
    public AiService createAiService(BclAiFrameworkProperties.ProviderConfig config, int timeout, int readTimeout) {
        return new OpenAiService(config, timeout, readTimeout);
    }

    @Override
    public int getPriority() {
        return 10; // 高优先级，OpenAI作为知名提供商
    }

    @Override
    public String getDescription() {
        return "OpenAI - 知名AI公司，提供GPT系列大语言模型";
    }

    @Override
    public List<String> getSupportedModels() {
        return SUPPORTED_MODELS;
    }
} 