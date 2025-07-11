package com.chow.easy.ai.framework.config;

import com.chow.easy.ai.framework.core.AiServiceFactory;
import com.chow.easy.ai.framework.util.AiServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BCL AI Framework 自动配置类
 * 当启用 bcl.ai-framework.enabled 时自动装配
 *
 * @author chowsama
 * @date 2025/06/26
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EasyAiFrameworkProperties.class)
@ConditionalOnProperty(prefix = "bcl.ai-framework", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EasyAiFrameworkAutoConfiguration {

    /**
     * 创建AI框架服务Bean
     *
     * @param easyAiFrameworkProperties 配置属性
     * @return AI框架服务实例
     */
    @Bean
    @ConditionalOnMissingBean
    public AiServiceFactory aiServiceFactory(EasyAiFrameworkProperties easyAiFrameworkProperties) {
        return new AiServiceFactory(easyAiFrameworkProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AiServiceUtil aiServiceUtil(AiServiceFactory aiServiceFactory) {
        return new AiServiceUtil(aiServiceFactory);
    }

    /**
     * 掩码API密钥，用于日志输出
     *
     * @param apiKey 原始API密钥
     * @return 掩码后的API密钥
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
