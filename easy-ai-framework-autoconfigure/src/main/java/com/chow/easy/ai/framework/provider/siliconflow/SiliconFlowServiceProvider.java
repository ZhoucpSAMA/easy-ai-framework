package com.chow.easy.ai.framework.provider.siliconflow;

import com.chow.easy.ai.framework.config.BclAiFrameworkProperties;
import com.chow.easy.ai.framework.core.AiService;
import com.chow.easy.ai.framework.spi.AiServiceProvider;

import java.util.Arrays;
import java.util.List;

/**
 * SiliconFlow AI服务提供商SPI实现
 *
 * @author chowsama
 * @date 2025/06/26
 */
public class SiliconFlowServiceProvider implements AiServiceProvider {

    private static final String PROVIDER_NAME = "siliconflow";

    private static final List<String> SUPPORTED_MODELS = Arrays.asList(
            "deepseek-ai/DeepSeek-V2.5",
            "deepseek-ai/DeepSeek-Coder-V2-Instruct",
            "deepseek-ai/DeepSeek-R1",
            "deepseek-ai/DeepSeek-V3",
            "Qwen/Qwen2.5-72B-Instruct",
            "Qwen/Qwen2.5-7B-Instruct",
            "Qwen/Qwen2-VL-7B-Instruct",
            "THUDM/glm-4-9b-chat",
            "internlm/internlm2_5-20b-chat",
            "meta-llama/Meta-Llama-3.1-8B-Instruct",
            "meta-llama/Meta-Llama-3.1-70B-Instruct"
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
        return new SiliconFlowService(config, timeout, readTimeout);
    }

    @Override
    public int getPriority() {
        return 50; // 中等优先级
    }

    @Override
    public String getDescription() {
        return "SiliconFlow (硅基流动) - 支持多种开源大语言模型的云服务平台";
    }

    @Override
    public List<String> getSupportedModels() {
        return SUPPORTED_MODELS;
    }
} 