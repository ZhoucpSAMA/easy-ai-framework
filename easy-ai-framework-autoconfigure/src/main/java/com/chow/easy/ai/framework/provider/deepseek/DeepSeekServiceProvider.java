package com.chow.easy.ai.framework.provider.deepseek;

import com.chow.easy.ai.framework.config.BclAiFrameworkProperties;
import com.chow.easy.ai.framework.core.AiService;
import com.chow.easy.ai.framework.spi.AiServiceProvider;

import java.util.Arrays;
import java.util.List;

/**
 * DeepSeek AI服务提供商SPI实现
 *
 * @author chowsama
 * @date 2025/06/26
 */
public class DeepSeekServiceProvider implements AiServiceProvider {

    private static final String PROVIDER_NAME = "deepseek";

    private static final List<String> SUPPORTED_MODELS = Arrays.asList(
            // 主要模型
            "deepseek-chat",      // DeepSeek V3 - 最新一代对话模型
            "deepseek-reasoner",  // DeepSeek R1 - 推理模型

            // 历史版本（为了兼容性）
            "deepseek-v3",
            "deepseek-v3-0324",
            "deepseek-v2.5",
            "deepseek-v2.5-1210",
            "deepseek-r1",
            "deepseek-r1-lite",

            // 代码相关模型
            "deepseek-coder",
            "deepseek-coder-v2-instruct",
            "deepseek-coder-v2-lite-instruct",

            // 数学模型
            "deepseek-math"
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
        return new DeepSeekService(config, timeout, readTimeout);
    }

    @Override
    public int getPriority() {
        return 20; // 中高优先级，仅次于OpenAI
    }

    @Override
    public String getDescription() {
        return "DeepSeek - 专注于AI大模型的公司，提供高质量的推理和代码生成模型";
    }

    @Override
    public List<String> getSupportedModels() {
        return SUPPORTED_MODELS;
    }
} 