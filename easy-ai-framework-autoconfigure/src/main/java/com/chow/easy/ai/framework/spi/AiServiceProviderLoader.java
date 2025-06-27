package com.chow.easy.ai.framework.spi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AI服务提供商SPI加载器
 * 负责加载和管理所有的AI服务提供商
 *
 * @author chowsama
 * @date 2025/06/26
 */
@Slf4j
public class AiServiceProviderLoader {

    private static final Map<String, AiServiceProvider> PROVIDER_CACHE = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;

    /**
     * 加载所有可用的AI服务提供商
     */
    public static synchronized void loadProviders() {
        if (loaded) {
            return;
        }

        log.info("开始加载AI服务提供商...");

        try {
            ServiceLoader<AiServiceProvider> serviceLoader = ServiceLoader.load(AiServiceProvider.class);
            List<AiServiceProvider> providers = new ArrayList<>();

            for (AiServiceProvider provider : serviceLoader) {
                providers.add(provider);
                log.info("发现AI服务提供商: {} - {}", provider.getProviderName(), provider.getDescription());
            }

            // 按优先级排序
            providers.sort(Comparator.comparingInt(AiServiceProvider::getPriority));

            // 缓存提供商
            for (AiServiceProvider provider : providers) {
                String providerName = provider.getProviderName();
                if (StringUtils.hasText(providerName)) {
                    PROVIDER_CACHE.put(providerName.toLowerCase(), provider);
                    log.info("注册AI服务提供商: {} (优先级: {})", providerName, provider.getPriority());
                } else {
                    log.warn("跳过无效的AI服务提供商，提供商名称为空: {}", provider.getClass().getName());
                }
            }

            loaded = true;
            log.info("AI服务提供商加载完成，共加载 {} 个提供商: {}",
                    PROVIDER_CACHE.size(),
                    PROVIDER_CACHE.keySet());

        } catch (Exception e) {
            log.error("加载AI服务提供商失败", e);
        }
    }

    /**
     * 根据提供商名称获取提供商实例
     *
     * @param providerName 提供商名称
     * @return 提供商实例，如果不存在则返回null
     */
    public static AiServiceProvider getProvider(String providerName) {
        loadProviders();

        if (!StringUtils.hasText(providerName)) {
            return null;
        }

        return PROVIDER_CACHE.get(providerName.toLowerCase());
    }

    /**
     * 获取所有已注册的提供商名称
     *
     * @return 提供商名称集合
     */
    public static Set<String> getAvailableProviders() {
        loadProviders();
        return new HashSet<>(PROVIDER_CACHE.keySet());
    }

    /**
     * 获取所有已注册的提供商实例
     *
     * @return 提供商实例列表
     */
    public static List<AiServiceProvider> getAllProviders() {
        loadProviders();
        return new ArrayList<>(PROVIDER_CACHE.values());
    }

    /**
     * 检查指定提供商是否可用
     *
     * @param providerName 提供商名称
     * @return true如果提供商可用
     */
    public static boolean isProviderAvailable(String providerName) {
        loadProviders();
        return StringUtils.hasText(providerName) &&
                PROVIDER_CACHE.containsKey(providerName.toLowerCase());
    }

    /**
     * 获取提供商信息摘要
     *
     * @return 提供商信息字符串
     */
    public static String getProvidersSummary() {
        loadProviders();

        if (PROVIDER_CACHE.isEmpty()) {
            return "未找到任何AI服务提供商";
        }

        return PROVIDER_CACHE.values().stream()
                .sorted(Comparator.comparingInt(AiServiceProvider::getPriority))
                .map(provider -> String.format("- %s (优先级: %d): %s",
                        provider.getProviderName(),
                        provider.getPriority(),
                        provider.getDescription()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 清除缓存，强制重新加载
     */
    public static synchronized void reload() {
        PROVIDER_CACHE.clear();
        loaded = false;
        loadProviders();
    }
} 