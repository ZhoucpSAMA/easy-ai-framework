package com.chow.easy.ai.framework.spi;

import com.chow.easy.ai.framework.config.BclAiFrameworkProperties;
import com.chow.easy.ai.framework.core.AiService;

/**
 * AI服务提供商SPI接口
 * 第三方可以通过实现此接口来扩展新的AI提供商
 *
 * @author chowsama
 * @date 2025/06/26
 */
public interface AiServiceProvider {

    /**
     * 获取提供商名称
     * 必须全局唯一，用于标识和配置
     *
     * @return 提供商名称，如 "siliconflow", "openai", "baidu" 等
     */
    String getProviderName();

    /**
     * 检查是否支持指定的提供商名称
     *
     * @param providerName 提供商名称
     * @return true如果支持该提供商
     */
    boolean supports(String providerName);

    /**
     * 创建AI服务实例
     *
     * @param config 提供商配置
     * @param timeout 连接超时时间（毫秒）
     * @param readTimeout 读取超时时间（毫秒）
     * @return AI服务实例
     */
    AiService createAiService(BclAiFrameworkProperties.ProviderConfig config, int timeout, int readTimeout);

    /**
     * 获取提供商优先级
     * 数值越小优先级越高，默认为100
     *
     * @return 优先级数值
     */
    default int getPriority() {
        return 100;
    }

    /**
     * 获取提供商描述信息
     *
     * @return 提供商描述
     */
    default String getDescription() {
        return "AI Service Provider: " + getProviderName();
    }

    /**
     * 获取支持的模型列表（可选）
     *
     * @return 支持的模型名称列表，null表示不限制
     */
    default java.util.List<String> getSupportedModels() {
        return null;
    }
} 