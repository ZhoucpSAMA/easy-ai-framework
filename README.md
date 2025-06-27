# BCL AI Framework Starter

<div align="center">

[![Java Version](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-2.0.0--SNAPSHOT-red.svg)](https://github.com/your-repo/bcl-ai-framework)

**🚀 企业级AI模型统一接入框架**

一个功能强大的Spring Boot Starter，为企业提供统一的AI模型API接入方案

[快速开始](#-快速开始) • [配置指南](#-配置指南) • [API文档](#-api接口) • [扩展开发](#-扩展开发)

</div>

## ✨ 功能特性

### 🎯 核心功能
- **🔌 开箱即用** - Spring Boot Starter，零配置快速集成
- **🌐 多提供商统一管理** - 支持OpenAI、DeepSeek、SiliconFlow、Doubao等主流AI服务
- **📝 统一API接口** - 无需学习各厂商差异化API，统一的编程接口
- **⚡ 同步/异步调用** - 支持传统同步调用和流式响应
- **🔄 SPI扩展机制** - 基于Java SPI，支持第三方提供商无缝扩展

### 🛠️ 技术特性
- **🎨 智能配置** - 完整的IDE配置提示支持，配置零出错
- **⏱️ 灵活超时控制** - 全局和提供商级别的超时配置
- **🔧 自动装配** - 基于条件的智能自动配置
- **📊 配置验证** - 启动时配置检查和连接测试
- **🏷️ 模型管理** - 支持获取各提供商的可用模型列表

## 🎯 支持的AI提供商

| 提供商 | 状态 | 主要模型 | 特色功能 |
|--------|------|----------|----------|
| **🤖 OpenAI** | ✅ 已实现 | GPT-4, GPT-3.5-turbo, GPT-4o | 业界标杆，功能完善 |
| **🧠 DeepSeek** | ✅ 已实现 | deepseek-chat, deepseek-coder | 推理能力强，代码生成 |
| **🌊 SiliconFlow** | ✅ 已实现 | Qwen2.5, GLM-4, Llama-3 | 开源模型云服务 |
| **🎪 Doubao(豆包)** | ✅ 已实现 | doubao-seed-1.6, doubao-pro | 字节跳动多模态模型 |
| **🐻 百度文心一言** | 🚧 开发中 | ERNIE系列 | - |
| **🌟 阿里通义千问** | 🚧 开发中 | Qwen系列 | - |
| **🔮 腾讯混元** | 🚧 开发中 | Hunyuan | - |

> 💡 **扩展提示**：框架基于SPI机制设计，您可以轻松扩展任何AI提供商

## 🚀 快速开始

### 📦 1. 添加依赖

在项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.xkw.bcl</groupId>
    <artifactId>bcl-ai-framework-starter</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```

### ⚙️ 2. 配置文件

在 `application.yml` 中配置AI提供商：

```yaml
bcl:
  ai-framework:
    # 是否启用框架（默认true）
    enabled: true
    
    # 默认使用的提供商
    default-provider: openai
    
    # 全局超时配置（毫秒）
    timeout: 30000
    read-timeout: 300000
    
    # 多提供商配置
    providers:
      # OpenAI配置
      openai:
        api-key: sk-your-openai-api-key
        api-url: https://api.openai.com/v1  # 可选
        default-model: gpt-3.5-turbo
        parameters:
          temperature: 0.7
          max_tokens: 2000
      
      # DeepSeek配置  
      deepseek:
        api-key: sk-your-deepseek-api-key
        default-model: deepseek-chat
        parameters:
          temperature: 1.0
          max_tokens: 4096
      
      # SiliconFlow配置
      siliconflow:
        api-key: sk-your-siliconflow-api-key
        default-model: Qwen/Qwen2.5-7B-Instruct
        parameters:
          temperature: 0.7
          max_tokens: 4096
```

### 💻 3. 代码使用

#### 基础用法

```java
@RestController
public class ChatController {
    
    @Autowired
    private AiServiceFactory serviceFactory;
    
    // 或者使用工具类
    @Autowired
    private AiServiceUtil aiServiceUtil;
    
    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        // 使用默认提供商
        return aiServiceUtil.chat(message);
        
        // 或者指定提供商
        // return aiServiceUtil.chat("openai", message);
    }
}
```

#### 高级用法

```java
@Service
public class AiService {
    
    @Autowired
    private AiServiceFactory serviceFactory;
    
    public void demonstrateAdvancedUsage() {
        // 获取特定提供商的服务
        AiService openaiService = serviceFactory.getService("openai");
        
        // 1. 简单对话
        String response = openaiService.chat("你好，请介绍一下你自己");
        
        // 2. 多轮对话
        List<AiMessage> messages = Arrays.asList(
            new AiMessage("user", "我正在学习Java编程"),
            new AiMessage("assistant", "很好！Java是一门强大的编程语言。"),
            new AiMessage("user", "能给我一个简单的例子吗？")
        );
        String multiResponse = openaiService.chat(messages);
        
        // 3. 流式对话
        openaiService.chatStream("请解释什么是机器学习", content -> {
            System.out.print(content); // 实时输出
        });
        
        // 4. 检查服务状态
        boolean available = openaiService.isAvailable();
        
        // 5. 获取支持的模型
        List<String> models = serviceFactory.getSupportedModels("openai");
    }
}
```

## 📋 配置指南

### 🔧 主要配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `bcl.ai-framework.enabled` | Boolean | `true` | 是否启用AI框架 |
| `bcl.ai-framework.default-provider` | String | `"openai"` | 默认使用的提供商 |
| `bcl.ai-framework.timeout` | Integer | `30000` | 全局请求超时时间（毫秒） |
| `bcl.ai-framework.read-timeout` | Integer | `300000` | 流式读取超时时间（毫秒） |
| `bcl.ai-framework.providers` | Map | - | 各提供商的详细配置 |

### 🏢 提供商配置

每个提供商支持以下配置：

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `api-key` | String | ✅ | API密钥 |
| `api-url` | String | ❌ | API基础URL（使用默认值） |
| `default-model` | String | ❌ | 默认使用的模型 |
| `parameters` | Map | ❌ | 提供商特定的参数配置 |

### 🎛️ 常用参数配置

```yaml
parameters:
  temperature: 0.7        # 创造性控制 (0.0-2.0)
  max_tokens: 2000       # 最大输出长度
  top_p: 1.0            # 核心采样参数
  frequency_penalty: 0.0 # 频率惩罚
  presence_penalty: 0.0  # 存在惩罚
  stop: ["Human:", "AI:"] # 停止词列表
```

## 📚 API接口

### 🏭 AiServiceFactory

AI服务工厂，用于创建和管理AI服务实例：

```java
public interface AiServiceFactory {
    // 获取默认AI服务
    AiService getDefaultService();
    
    // 获取指定提供商的AI服务
    AiService getService(String providerName);
    
    // 检查提供商是否已配置
    boolean isProviderConfigured(String providerName);
    
    // 获取已配置的提供商
    Set<String> getConfiguredProviders();
    
    // 获取可用的SPI提供商
    Set<String> getAvailableSpiProviders();
    
    // 获取支持的模型列表
    List<String> getSupportedModels(String providerName);
    
    // 缓存管理
    void clearCache();
    void clearCache(String providerName);
}
```

### 🤖 AiService

统一的AI服务接口：

```java
public interface AiService {
    // 简单对话
    String chat(String prompt);
    
    // 多轮对话
    String chat(List<AiMessage> messages);
    
    // 流式对话
    void chatStream(String prompt, Consumer<String> callback);
    void chatStream(List<AiMessage> messages, Consumer<String> callback);
    
    // 获取提供商名称
    String getProviderName();
    
    // 检查服务可用性
    boolean isAvailable();
}
```

### 🛠️ AiServiceUtil

便捷工具类：

```java
public class AiServiceUtil {
    // 使用默认提供商对话
    String chat(String prompt);
    
    // 使用指定提供商对话
    String chat(String providerName, String prompt);
    String chat(String providerName, List<AiMessage> messages);
    
    // 流式对话
    void chatStream(String prompt, Consumer<String> callback);
    void chatStream(String providerName, String prompt, Consumer<String> callback);
}
```

## 🔌 扩展开发

### 📝 实现自定义提供商

框架基于Java SPI机制，支持第三方扩展。以下是完整的扩展步骤：

#### 1️⃣ 实现 AiServiceProvider 接口

```java
public class CustomAiServiceProvider implements AiServiceProvider {
    
    @Override
    public String getProviderName() {
        return "custom-ai";
    }
    
    @Override
    public boolean supports(String providerName) {
        return "custom-ai".equalsIgnoreCase(providerName);
    }
    
    @Override
    public AiService createAiService(
            BclAiFrameworkProperties.ProviderConfig config, 
            int timeout, 
            int readTimeout) {
        return new CustomAiService(config, timeout, readTimeout);
    }
    
    @Override
    public int getPriority() {
        return 50; // 优先级，数值越小越高
    }
    
    @Override
    public String getDescription() {
        return "Custom AI Provider - 自定义AI服务提供商";
    }
    
    @Override
    public List<String> getSupportedModels() {
        return Arrays.asList("custom-model-v1", "custom-model-v2");
    }
}
```

#### 2️⃣ 实现 AiService 接口

```java
public class CustomAiService extends BaseAiServiceImpl {
    
    public CustomAiService(BclAiFrameworkProperties.ProviderConfig config, 
                          int timeout, int readTimeout) {
        super(config, timeout, readTimeout);
    }
    
    @Override
    public String getProviderName() {
        return "custom-ai";
    }
    
    @Override
    protected Map<String, Object> buildRequestBody(List<AiMessage> messages, boolean stream) {
        // 构建请求体
        return requestBody;
    }
    
    @Override
    protected String parseResponse(String responseBody) {
        // 解析响应
        return parsedContent;
    }
    
    @Override
    protected void parseStreamResponse(String line, Consumer<String> callback) {
        // 解析流式响应
        callback.accept(content);
    }
    
    @Override
    protected String getApiEndpoint() {
        return config.getApiUrl() + "/chat/completions";
    }
}
```

#### 3️⃣ 注册SPI服务

在 `src/main/resources/META-INF/services/com.xkw.bcl.ai.framework.spi.AiServiceProvider` 文件中添加：

```
com.example.provider.CustomAiServiceProvider
```

#### 4️⃣ 配置使用

```yaml
bcl:
  ai-framework:
    providers:
      custom-ai:
        api-key: your-custom-api-key
        api-url: https://api.custom-ai.com
        default-model: custom-model-v1
```

> 📖 **详细指南**：查看 [SPI-GUIDE.md](SPI-GUIDE.md) 获取完整的扩展开发指南

## 🎯 提供商使用指南

- 📘 [OpenAI 使用指南](OPENAI-PROVIDER-GUIDE.md) - GPT系列模型完整配置
- 📗 [DeepSeek 使用指南](DEEPSEEK-PROVIDER-GUIDE.md) - 推理和代码生成专家
- 📙 [Doubao 使用指南](DOUBAO-PROVIDER-GUIDE.md) - 字节跳动多模态模型

## 🔧 最佳实践

### 💡 性能优化

```yaml
# 生产环境推荐配置
bcl:
  ai-framework:
    timeout: 60000      # 适当增加超时
    read-timeout: 180000 # 流式响应超时
    providers:
      openai:
        parameters:
          temperature: 0.3  # 降低随机性
          max_tokens: 1000  # 控制输出长度
```

### 🔒 安全配置

```yaml
# 使用环境变量保护API密钥
bcl:
  ai-framework:
    providers:
      openai:
        api-key: ${OPENAI_API_KEY}
      deepseek:
        api-key: ${DEEPSEEK_API_KEY}
```

### 📊 监控调试

```yaml
# 启用详细日志
logging:
  level:
    com.xkw.bcl.ai.framework: DEBUG
```

## 🚨 常见问题

### Q: 启动时提示"Provider not configured"？
**A:** 检查 `application.yml` 中是否正确配置了 `api-key`，确保不为空。

### Q: 如何切换不同的提供商？
**A:** 修改 `default-provider` 配置，或在代码中使用 `serviceFactory.getService("provider-name")`。

### Q: 支持代理访问吗？
**A:** 支持，在 `api-url` 中配置代理地址即可。

### Q: 如何获取错误详情？
**A:** 启用DEBUG日志级别，查看详细的请求响应信息。

## 🗺️ 开发路线图

### ✅ 已完成
- [x] 多提供商配置管理
- [x] SPI扩展机制  
- [x] OpenAI、DeepSeek、SiliconFlow、Doubao提供商实现
- [x] 同步和流式API调用
- [x] 自动装配和条件注入
- [x] 完整的配置验证

### 🚧 开发中
- [ ] 百度文心一言API实现
- [ ] 阿里通义千问API实现  
- [ ] 腾讯混元API实现
- [ ] Spring Boot 3.x 支持

### 🔮 计划中
- [ ] 多模态（图片、语音）支持
- [ ] 负载均衡和故障转移
- [ ] 请求缓存机制
- [ ] 调用统计和监控
- [ ] 异步批量处理

## 🤝 贡献指南

我们欢迎任何形式的贡献！

1. **🍴 Fork** 项目
2. **🌿 创建** 特性分支 (`git checkout -b feature/AmazingFeature`)
3. **💍 提交** 更改 (`git commit -m 'Add some AmazingFeature'`)
4. **📤 推送** 分支 (`git push origin feature/AmazingFeature`)
5. **🎉 创建** Pull Request

## 📄 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

感谢所有贡献者的辛勤工作，以及以下开源项目的支持：

- [Spring Boot](https://spring.io/projects/spring-boot) - 应用框架
- [OkHttp](https://square.github.io/okhttp/) - HTTP客户端
- [Jackson](https://github.com/FasterXML/jackson) - JSON处理

---

<div align="center">

**🌟 如果这个项目对您有帮助，请给我们一个Star！ 🌟**

[⬆ 回到顶部](#bcl-ai-framework-starter)

</div> 