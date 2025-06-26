# BCL AI Framework SPI 机制使用指南

BCL AI Framework 现已支持 Java SPI (Service Provider Interface) 机制，允许第三方开发者轻松扩展新的 AI 服务提供商，无需修改框架核心代码。

## 🚀 SPI 机制概述

SPI (Service Provider Interface) 是 Java 提供的一种服务发现机制，允许在运行时动态发现和加载服务实现。

### 优势
- ✅ **解耦合**: 核心框架与具体实现完全分离
- ✅ **可扩展**: 第三方可以独立开发新的提供商
- ✅ **热插拔**: 支持运行时动态加载和卸载
- ✅ **标准化**: 遵循 Java 标准的 SPI 规范

## 🔧 如何开发新的 AI 提供商

### 1. 实现 AiServiceProvider 接口

```java
package com.example.provider;

import com.xkw.bcl.ai.framework.spi.AiServiceProvider;
import com.xkw.bcl.ai.framework.config.BclAiFrameworkProperties;
import com.xkw.bcl.ai.framework.core.AiService;

public class CustomAiServiceProvider implements AiServiceProvider {
    
    @Override
    public String getProviderName() {
        return "custom-ai"; // 提供商唯一名称
    }
    
    @Override
    public boolean supports(String providerName) {
        return "custom-ai".equalsIgnoreCase(providerName);
    }
    
    @Override
    public AiService createAiService(BclAiFrameworkProperties.ProviderConfig config, 
                                   int timeout, int readTimeout) {
        return new CustomAiService(config, timeout, readTimeout);
    }
    
    @Override
    public int getPriority() {
        return 80; // 优先级，数值越小优先级越高
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

### 2. 实现具体的 AI 服务

```java
package com.example.provider;

import com.xkw.bcl.ai.framework.core.BaseAiServiceImpl;
import com.xkw.bcl.ai.framework.core.AiMessage;
import com.xkw.bcl.ai.framework.config.BclAiFrameworkProperties;

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
    public boolean isAvailable() {
        // 实现可用性检查逻辑
        return true;
    }
    
    @Override
    protected Map<String, Object> buildRequestBody(List<AiMessage> messages, boolean stream) {
        // 构建API请求体
        Map<String, Object> body = new HashMap<>();
        body.put("messages", messages);
        body.put("stream", stream);
        return body;
    }
    
    @Override
    protected String parseResponse(String responseBody) {
        // 解析API响应
        // 实现具体的解析逻辑
        return responseBody;
    }
    
    @Override
    protected void parseStreamResponse(String line, Consumer<String> callback) {
        // 解析流式响应
        // 实现具体的流式解析逻辑
        callback.accept(line);
    }
    
    @Override
    protected String getApiEndpoint() {
        return "/v1/chat/completions"; // API端点
    }
}
```

### 3. 创建 SPI 配置文件

在你的 JAR 包中创建文件：
```
src/main/resources/META-INF/services/com.xkw.bcl.ai.framework.spi.AiServiceProvider
```

文件内容：
```
# 自定义AI服务提供商
com.example.provider.CustomAiServiceProvider
```

### 4. 打包和部署

```bash
# 编译打包
mvn clean package

# 将JAR包添加到classpath即可自动发现
```

## 📦 现有的 SPI 提供商

### SiliconFlow 提供商
- **名称**: `siliconflow`
- **类**: `com.xkw.bcl.ai.framework.provider.siliconflow.SiliconFlowServiceProvider`
- **优先级**: 50
- **支持模型**: DeepSeek-V2.5, Qwen2.5, GLM-4 等多种开源模型

## ⚙️ 配置示例

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: custom-ai  # 使用自定义提供商
    providers:
      custom-ai:
        api-key: your-custom-api-key
        api-url: https://api.custom-ai.com
        default-model: custom-model-v1
        parameters:
          temperature: 0.7
          max_tokens: 2000
```

## 🔍 使用和测试

```java
@Autowired
private AiServiceFactory serviceFactory;

public void useCustomProvider() {
    // 获取自定义提供商服务
    AiService customService = serviceFactory.getService("custom-ai");
    
    // 使用服务
    String response = customService.chat("Hello, custom AI!");
    System.out.println("Custom AI 回复: " + response);
}

public void listAllProviders() {
    // 获取所有可用的SPI提供商
    Set<String> providers = serviceFactory.getAvailableSpiProviders();
    System.out.println("可用提供商: " + providers);
    
    // 获取提供商详情
    for (String provider : providers) {
        List<String> models = serviceFactory.getSupportedModels(provider);
        System.out.println(provider + " 支持的模型: " + models);
    }
}
```

## 🛠️ 高级功能

### 1. 优先级控制

```java
@Override
public int getPriority() {
    return 10; // 高优先级，会优先选择此提供商
}
```

### 2. 动态重载

```java
// 重新加载所有SPI提供商
serviceFactory.reloadSpiProviders();
```

### 3. 条件支持

```java
@Override
public boolean supports(String providerName) {
    // 可以支持多个名称或别名
    return "custom-ai".equalsIgnoreCase(providerName) || 
           "custom".equalsIgnoreCase(providerName);
}
```

## 📋 最佳实践

### 1. 命名规范
- 提供商名称使用小写，用连字符分隔：`my-ai-provider`
- 类名使用驼峰命名：`MyAiServiceProvider`

### 2. 错误处理
```java
@Override
public AiService createAiService(ProviderConfig config, int timeout, int readTimeout) {
    try {
        validateConfig(config);
        return new MyAiService(config, timeout, readTimeout);
    } catch (Exception e) {
        throw new IllegalArgumentException("创建服务失败: " + e.getMessage(), e);
    }
}

private void validateConfig(ProviderConfig config) {
    if (config.getApiKey() == null) {
        throw new IllegalArgumentException("API Key 不能为空");
    }
    // 其他验证逻辑...
}
```

### 3. 日志记录
```java
@Override
public AiService createAiService(ProviderConfig config, int timeout, int readTimeout) {
    log.info("创建 {} 服务实例，模型: {}", getProviderName(), config.getDefaultModel());
    return new MyAiService(config, timeout, readTimeout);
}
```

### 4. 资源管理
```java
public class MyAiService extends BaseAiServiceImpl implements AutoCloseable {
    
    @Override
    public void close() throws Exception {
        // 清理资源，如关闭连接池等
    }
}
```

## 🔗 参考链接

- [Java SPI 官方文档](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
- [BCL AI Framework API 文档](./README.md)
- [SiliconFlow 提供商示例](./bcl-ai-framework-autoconfigure/src/main/java/com/xkw/bcl/ai/framework/provider/siliconflow/)

通过 SPI 机制，BCL AI Framework 真正实现了"开放式架构"，任何人都可以为框架贡献新的 AI 提供商实现！🚀 