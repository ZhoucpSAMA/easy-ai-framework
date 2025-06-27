# BCL AI Framework Starter

BCL AI Framework Starter 是一个Spring Boot自动配置组件，用于快速集成和调用各大厂商的AI模型API。支持多提供商配置，统一的API接口。

## 功能特性

- 🚀 开箱即用的Spring Boot Starter
- 🔧 多提供商统一配置和管理
- ⚙️ 基于YAML的简单配置
- 🔄 自动装配和条件注入
- 📝 完整的IDE配置提示支持
- ⏱️ 灵活的超时配置
- 🎯 提供商切换和负载均衡支持

## 支持的AI提供商

- 🤖 **OpenAI** - GPT系列模型
- 🐻 **百度文心一言** - ERNIE系列模型
- 🌟 **阿里通义千问** - Qwen系列模型
- 🔮 **腾讯混元** - Hunyuan模型
- 📈 更多提供商持续添加中...

## 快速开始

### 1. 添加依赖

在您的`pom.xml`中添加依赖：

```xml
<dependency>
    <groupId>com.xkw.bcl</groupId>
    <artifactId>bcl-ai-framework-starter</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```

### 2. 配置文件

在`application.yml`中添加多提供商配置：

```yaml
bcl:
  ai-framework:
    # 是否启用框架
    enabled: true
    
    # 默认使用的提供商
    default-provider: openai
    
    # 全局超时配置
    timeout: 30000
    read-timeout: 300000
    
    # 多提供商配置
    providers:
      # OpenAI配置
      openai:
        api-key: your-openai-api-key
        api-url: https://api.openai.com/v1
        default-model: gpt-3.5-turbo
        parameters:
          temperature: 0.7
          max-tokens: 2000
      
      # 百度文心一言配置
      baidu:
        api-key: your-baidu-api-key
        api-url: https://aip.baidubce.com/rpc/2.0/ai_custom/v1
        default-model: ernie-bot
        parameters:
          temperature: 0.8
          top-p: 0.8
      
      # 阿里通义千问配置
      alibaba:
        api-key: your-alibaba-api-key
        api-url: https://dashscope.aliyuncs.com/api/v1
        default-model: qwen-turbo
```

### 3. 使用服务

注入`AiFrameworkService`即可使用：

```java
@Autowired
private AiFrameworkService aiFrameworkService;

public void demonstrateUsage() {
    // 检查服务配置状态
    if (aiFrameworkService.isConfigured()) {
        
        // 获取可用的提供商
        Set<String> providers = aiFrameworkService.getAvailableProviders();
        System.out.println("可用提供商: " + providers);
        
        // 使用默认提供商测试连接
        boolean connected = aiFrameworkService.testConnection();
        
        // 切换到特定提供商测试
        boolean baiduConnected = aiFrameworkService.testConnection("baidu");
        
        // 获取配置摘要
        String summary = aiFrameworkService.getConfigSummary();
        System.out.println(summary);
    }
}
```

## 配置说明

### 主要配置项

| 配置项                                 | 类型      | 默认值    | 说明           |
|-------------------------------------|---------|--------|--------------|
| `bcl.ai-framework.enabled`          | Boolean | true   | 是否启用AI框架     |
| `bcl.ai-framework.default-provider` | String  | openai | 默认使用的提供商     |
| `bcl.ai-framework.timeout`          | Integer | 30000  | 全局请求超时时间（毫秒） |
| `bcl.ai-framework.read-timeout`     | Integer | 300000 | 流式读取超时时间（毫秒） |
| `bcl.ai-framework.providers`        | Map     | -      | 各提供商的详细配置    |

### 提供商配置项

每个提供商支持以下配置：

| 配置项             | 类型     | 说明           |
|-----------------|--------|--------------|
| `api-key`       | String | **必填** API密钥 |
| `api-url`       | String | API基础URL     |
| `default-model` | String | 默认使用的模型      |
| `parameters`    | Map    | 提供商特定的参数配置   |

## 配置示例

### 单提供商配置

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: openai
    providers:
      openai:
        api-key: sk-your-openai-key
        api-url: https://api.openai.com/v1
        default-model: gpt-3.5-turbo
```

### 多提供商配置

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: openai
    providers:
      openai:
        api-key: sk-your-openai-key
        default-model: gpt-4
      baidu:
        api-key: your-baidu-key
        default-model: ernie-bot
      alibaba:
        api-key: your-alibaba-key
        default-model: qwen-turbo
```

## 自动配置条件

框架在以下条件满足时自动配置：

1. ✅ `bcl.ai-framework.enabled=true`（默认启用）
2. ✅ 至少配置了一个提供商

## 禁用框架

```yaml
bcl:
  ai-framework:
    enabled: false  # 显式禁用
```

## API接口预览

```java
public interface AiFrameworkService {
    // 配置管理
    boolean isConfigured();
    Set<String> getAvailableProviders();
    String getDefaultProvider();
    
    // 连接测试
    boolean testConnection();
    boolean testConnection(String provider);
    
    // 配置获取
    ProviderConfig getProviderConfig(String provider);
    String getConfigSummary();
    
    // TODO: AI调用接口（开发中）
    // String chat(String message);
    // String chat(String provider, String message);
    // Stream<String> chatStream(String message);
}
```

## 开发计划

- [x] 多提供商配置管理
- [x] 自动装配和条件注入
- [x] 配置验证和连接测试
- [ ] OpenAI API调用实现
- [ ] 百度文心一言API调用实现
- [ ] 阿里通义千问API调用实现
- [ ] 腾讯混元API调用实现
- [ ] 统一的对话接口
- [ ] 流式响应支持
- [ ] 异步调用支持
- [ ] 负载均衡和故障转移

## 许可证

本项目使用MIT许可证。 