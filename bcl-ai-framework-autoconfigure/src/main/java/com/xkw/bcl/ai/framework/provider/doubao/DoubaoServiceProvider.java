package com.xkw.bcl.ai.framework.provider.doubao;

import com.xkw.bcl.ai.framework.config.BclAiFrameworkProperties;
import com.xkw.bcl.ai.framework.core.AiService;
import com.xkw.bcl.ai.framework.spi.AiServiceProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Doubao（豆包）AI服务提供商SPI实现
 *
 * @author chowsama
 * @date 2025/06/26
 */
public class DoubaoServiceProvider implements AiServiceProvider {

    private static final String PROVIDER_NAME = "doubao";

    private static final List<String> SUPPORTED_MODELS = Arrays.asList(
            // 推荐模型 - 最新一代
            "doubao-seed-1.6",           // 全新多模态深度思考模型
            "doubao-seed-1.6-flash",     // 极致推理速度的多模态模型
            "doubao-seed-1.6-thinking",  // 强化思考能力的模型

            // 主流模型
            "doubao-1.5-pro-32k",        // Pro版本，32k上下文
            "doubao-1.5-pro-256k",       // Pro版本，256k上下文
            "doubao-1.5-lite",           // Lite版本，高性价比
            "doubao-1.5-thinking-pro",   // 深度思考Pro版本

            // 多模态模型
            "doubao-1.5-vision-pro",     // 视觉理解Pro版本
            "doubao-1.5-vision-lite",    // 视觉理解Lite版本
            "doubao-vision-pro",         // 视觉理解模型
            "doubao-vision-lite",        // 视觉理解轻量版

            // 历史版本
            "doubao-pro-32k",            // 历史Pro版本
            "doubao-pro-256k",           // 历史Pro版本，长上下文
            "doubao-lite-4k",            // 历史Lite版本，4k上下文
            "doubao-lite-32k",           // 历史Lite版本，32k上下文
            "doubao-lite-128k",          // 历史Lite版本，128k上下文

            // 专用模型
            "doubao-1.5-ui-tars",        // UI任务处理模型
            "doubao-seedance-1.0-lite",  // 视频生成Lite版
            "doubao-seedance-1.0-pro",   // 视频生成Pro版
            "doubao-seedream-3.0-t2i",   // 图片生成模型

            // 向量化模型
            "doubao-embedding",          // 文本向量化
            "doubao-embedding-large",    // 大型向量化模型
            "doubao-embedding-vision",   // 图文向量化模型

            // 其他模型
            "wan2.1-14b"                 // 万2.1模型
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
        return new DoubaoService(config, timeout, readTimeout);
    }

    @Override
    public int getPriority() {
        return 30; // 中等优先级，在OpenAI和DeepSeek之后
    }

    @Override
    public String getDescription() {
        return "Doubao - 字节跳动推出的豆包大模型，支持多模态、长上下文和深度思考功能";
    }

    @Override
    public List<String> getSupportedModels() {
        return SUPPORTED_MODELS;
    }
} 