package com.chow.easy.ai.framework.core;

import com.chow.easy.ai.framework.config.EasyAiFrameworkProperties;
import com.chow.easy.ai.framework.provider.deepseek.DeepSeekService;
import com.chow.easy.ai.framework.provider.doubao.DoubaoService;
import com.chow.easy.ai.framework.provider.openai.OpenAiService;
import com.chow.easy.ai.framework.provider.siliconflow.SiliconFlowService;
import com.chow.easy.ai.framework.spi.AiServiceProvider;
import com.chow.easy.ai.framework.spi.AiServiceProviderLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI服务工厂类
 * 负责创建和缓存各种AI服务提供商的实例
 * 支持SPI机制动态加载提供商
 *
 * @author chowsama
 * @date 2025/06/26
 */
@Slf4j
@Component
public class AiServiceFactory {
    private final EasyAiFrameworkProperties aiProperties;
    private final Map<String, AiService> serviceCache = new ConcurrentHashMap<>();

    public AiServiceFactory(EasyAiFrameworkProperties aiProperties) {
        this.aiProperties = aiProperties;

        // 初始化时加载SPI提供商
        AiServiceProviderLoader.loadProviders();

        log.info("AiServiceFactory 初始化完成");
        log.info("默认提供商: {}", aiProperties.getDefaultProvider());
        log.info("可用的SPI提供商: {}", AiServiceProviderLoader.getAvailableProviders());
        log.info("SPI提供商详情:\n{}", AiServiceProviderLoader.getProvidersSummary());
    }

    /**
     * 获取默认AI服务
     */
    public AiService getDefaultService() {
        return getService(aiProperties.getDefaultProvider());
    }

    /**
     * 获取指定提供商的AI服务
     */
    public AiService getService(String providerName) {
        return serviceCache.computeIfAbsent(providerName, this::createService);
    }

    /**
     * 检查指定提供商是否已配置
     */
    public boolean isProviderConfigured(String providerName) {
        EasyAiFrameworkProperties.ProviderConfig config = aiProperties.getProviders().get(providerName);
        return config != null &&
                config.getApiKey() != null &&
                !config.getApiKey().trim().isEmpty();
    }

    /**
     * 获取所有已配置的提供商名称
     */
    public Set<String> getConfiguredProviders() {
        return aiProperties.getProviders().keySet();
    }

    /**
     * 获取所有可用的SPI提供商名称
     */
    public Set<String> getAvailableSpiProviders() {
        return AiServiceProviderLoader.getAvailableProviders();
    }

    /**
     * 创建AI服务实例
     * 优先使用SPI机制，如果SPI中没有找到则回退到硬编码方式
     */
    private AiService createService(String providerName) {
        EasyAiFrameworkProperties.ProviderConfig config = aiProperties.getProviders().get(providerName);
        if (config == null) {
            throw new IllegalArgumentException("Provider not configured: " + providerName);
        }

        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            throw new IllegalArgumentException("API key not configured for provider: " + providerName);
        }

        log.info("创建 {} 服务实例", providerName);

        // 优先使用SPI机制
        AiServiceProvider spiProvider = AiServiceProviderLoader.getProvider(providerName);
        if (spiProvider != null) {
            log.info("使用SPI提供商创建服务: {} - {}", providerName, spiProvider.getDescription());
            return spiProvider.createAiService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());
        }

        // 回退到硬编码方式（兼容性保证）
        log.info("SPI中未找到提供商 {}，使用硬编码方式创建", providerName);
        switch (providerName.toLowerCase()) {
            case "siliconflow":
                return new SiliconFlowService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());
            case "openai":
                return new OpenAiService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());
            case "deepseek":
                return new DeepSeekService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());
            case "doubao":
                return new DoubaoService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());

            // TODO: 添加其他提供商的实现
            // case "baidu":
            //     return new BaiduService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());
            // case "alibaba":
            //     return new AlibabaService(config, aiProperties.getTimeout(), aiProperties.getReadTimeout());

            default:
                throw new IllegalArgumentException("Unsupported provider: " + providerName +
                        ". 可用的SPI提供商: " + AiServiceProviderLoader.getAvailableProviders());
        }
    }

    /**
     * 获取提供商支持的模型列表
     *
     * @param providerName 提供商名称
     * @return 支持的模型列表，如果提供商不存在或不支持则返回null
     */
    public java.util.List<String> getSupportedModels(String providerName) {
        AiServiceProvider spiProvider = AiServiceProviderLoader.getProvider(providerName);
        return spiProvider != null ? spiProvider.getSupportedModels() : null;
    }

    /**
     * 清除服务缓存
     */
    public void clearCache() {
        serviceCache.clear();
        log.info("AI服务缓存已清空");
    }

    /**
     * 清除指定提供商的服务缓存
     */
    public void clearCache(String providerName) {
        serviceCache.remove(providerName);
        log.info("提供商 {} 的服务缓存已清空", providerName);
    }

    /**
     * 重新加载SPI提供商
     */
    public void reloadSpiProviders() {
        log.info("重新加载SPI提供商...");
        AiServiceProviderLoader.reload();
        clearCache(); // 清除缓存，确保使用新加载的提供商
        log.info("SPI提供商重新加载完成，可用提供商: {}", AiServiceProviderLoader.getAvailableProviders());
    }
}
