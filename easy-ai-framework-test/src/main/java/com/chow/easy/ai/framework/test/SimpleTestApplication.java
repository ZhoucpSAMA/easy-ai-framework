package com.chow.easy.ai.framework.test;

import com.chow.easy.ai.framework.core.AiMessage;
import com.chow.easy.ai.framework.core.AiService;
import com.chow.easy.ai.framework.core.AiServiceFactory;
import com.chow.easy.ai.framework.spi.AiServiceProviderLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;

/**
 * BCL AI Framework 测试应用程序
 * 用于验证SPI机制和SiliconFlow提供商的功能
 *
 * @author chowsama
 * @date 2025/06/26
 */
@SpringBootApplication
public class SimpleTestApplication {

    public static void main(String[] args) {
        System.out.println("=== BCL AI Framework SPI机制 + SiliconFlow 提供商测试 ===");
        
        try {
            ConfigurableApplicationContext context = SpringApplication.run(SimpleTestApplication.class, args);

            System.out.println("✅ 应用启动成功");
            System.out.println("📋 加载的Bean数量: " + context.getBeanDefinitionCount());

            // 检查AI服务工厂是否被加载
            try {
                AiServiceFactory serviceFactory = context.getBean(AiServiceFactory.class);
                System.out.println("✅ AiServiceFactory 已加载");

                // 展示SPI功能
                showSpiInfo(serviceFactory);

                // 显示已配置的提供商
                System.out.println("\n🔧 已配置的提供商: " + serviceFactory.getConfiguredProviders());

                // 测试各提供商配置
                System.out.println("\n📊 提供商配置测试:");
                for (String provider : serviceFactory.getConfiguredProviders()) {
                    boolean configured = serviceFactory.isProviderConfigured(provider);
                    System.out.printf("  - %s: 配置状态=%s%n",
                            provider,
                            configured ? "✅" : "❌");

                    if (configured) {
                        testProvider(serviceFactory, provider);
                    }
                }

            } catch (Exception e) {
                System.out.println("⚠️  AiServiceFactory 未加载: " + e.getMessage());
                System.out.println("请检查配置文件中是否正确配置了 bcl.ai-framework");
            }

            System.out.println("\n=== 测试完成 ===");

        } catch (Exception e) {
            System.err.println("❌ 启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 展示SPI功能信息
     */
    private static void showSpiInfo(AiServiceFactory serviceFactory) {
        System.out.println("\n🔌 SPI机制信息:");
        System.out.println("📦 可用的SPI提供商: " + serviceFactory.getAvailableSpiProviders());

        System.out.println("\n📋 SPI提供商详细信息:");
        String providersSummary = AiServiceProviderLoader.getProvidersSummary();
        System.out.println(providersSummary);

        // 展示支持的模型列表
        System.out.println("\n🎯 各提供商支持的模型:");
        for (String providerName : serviceFactory.getAvailableSpiProviders()) {
            List<String> supportedModels = serviceFactory.getSupportedModels(providerName);
            if (supportedModels != null && !supportedModels.isEmpty()) {
                System.out.printf("  📱 %s 支持的模型:\n", providerName);
                for (int i = 0; i < Math.min(supportedModels.size(), 5); i++) {
                    System.out.printf("    - %s\n", supportedModels.get(i));
                }
                if (supportedModels.size() > 5) {
                    System.out.printf("    ... 等共 %d 个模型\n", supportedModels.size());
                }
            } else {
                System.out.printf("  📱 %s: 无限制或未指定模型列表\n", providerName);
            }
        }
    }

    /**
     * 测试指定提供商的功能
     */
    private static void testProvider(AiServiceFactory serviceFactory, String provider) {
        try {
            System.out.printf("\n🧪 测试提供商: %s%n", provider);

            // 获取服务实例
            AiService aiService = serviceFactory.getService(provider);
            System.out.printf("  📱 服务实例: %s%n", aiService.getClass().getSimpleName());
            System.out.printf("  🏷️  提供商名称: %s%n", aiService.getProviderName());

            // 检查服务可用性
            boolean available = aiService.isAvailable();
            System.out.printf("  🔍 服务可用性: %s%n", available ? "✅" : "❌");

            if (available) {
                // 测试简单对话
                try {
                    String response = aiService.chat("你好，请简单介绍一下你自己");
                    System.out.printf("  💬 对话测试: ✅%n");
                    System.out.printf("  📝 响应内容: %s%n",
                            response.length() > 100 ? response.substring(0, 100) + "..." : response);
                } catch (Exception e) {
                    System.out.printf("  💬 对话测试: ❌ (%s)%n", e.getMessage());
                }

                // 测试多轮对话
                try {
                    List<AiMessage> messages = Arrays.asList(
                            new AiMessage("user", "我的名字是张三"),
                            new AiMessage("assistant", "你好张三，很高兴认识你！"),
                            new AiMessage("user", "你还记得我的名字吗？")
                    );
                    String response = aiService.chat(messages);
                    System.out.printf("  🔄 多轮对话测试: ✅%n");
                    System.out.printf("  📝 响应内容: %s%n",
                            response.length() > 100 ? response.substring(0, 100) + "..." : response);
                } catch (Exception e) {
                    System.out.printf("  🔄 多轮对话测试: ❌ (%s)%n", e.getMessage());
                }

                // 测试流式对话
                try {
                    System.out.printf("  🌊 流式对话测试: ");
                    StringBuilder streamResponse = new StringBuilder();
                    aiService.chatStream("请简单介绍AI技术", content -> {
                        streamResponse.append(content);
                        System.out.print(".");
                    });
                    System.out.printf(" ✅%n");
                    System.out.printf("  📝 流式响应: %s%n",
                            streamResponse.length() > 100 ?
                                    streamResponse.substring(0, 100) + "..." : streamResponse.toString());
                } catch (Exception e) {
                    System.out.printf(" ❌ (%s)%n", e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.printf("  ❌ 提供商测试失败: %s%n", e.getMessage());
        }
    }
} 