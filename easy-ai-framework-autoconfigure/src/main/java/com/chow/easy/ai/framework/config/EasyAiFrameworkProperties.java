package com.chow.easy.ai.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * BCL AI Framework 配置属性
 * 用于读取 application.yml 中的 bcl.ai-framework 配置
 *
 * @author chowsama
 * @date 2025/06/26
 */
@Data
@ConfigurationProperties(prefix = "bcl.ai-framework")
public class EasyAiFrameworkProperties {

    /**
     * 是否启用AI功能
     */
    private boolean enabled = true;

    /**
     * 默认提供商
     */
    private String defaultProvider = "openai";

    /**
     * 全局超时时间(毫秒)
     */
    private int timeout = 30000;

    /**
     * 流式读取超时时间(毫秒)
     */
    private int readTimeout = 300000;

    /**
     * 各提供商配置
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    @Data
    public static class ProviderConfig {
        /**
         * API Key
         */
        private String apiKey;

        /**
         * API URL
         */
        private String apiUrl;

        /**
         * 默认模型
         */
        private String defaultModel;

        /**
         * 自定义参数
         */
        private Map<String, Object> parameters = new HashMap<>();

    }
} 