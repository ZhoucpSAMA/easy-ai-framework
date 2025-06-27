# BCL AI Framework - DeepSeek 提供商使用指南

本文档介绍如何在 BCL AI Framework 中使用 DeepSeek 提供商。

## 🚀 快速开始

### 1. 配置 DeepSeek API Key

在 `application.yml` 中配置：

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: deepseek
    providers:
      deepseek:
        api-key: sk-your-deepseek-api-key-here
        default-model: deepseek-chat
        parameters:
          temperature: 1.0
          max_tokens: 4096
```

### 2. 获取 API Key

1. 访问 [DeepSeek Platform](https://platform.deepseek.com/)
2. 注册或登录账户
3. 在 API Keys 页面创建新的 API Key
4. 将 API Key 配置到项目中

### 3. 基本使用

```java
@Autowired
private AiServiceFactory serviceFactory;

public void useDeepSeek() {
    AiService deepseekService = serviceFactory.getService("deepseek");
    
    // 简单对话
    String response = deepseekService.chat("你好，请介绍一下你自己");
    System.out.println("DeepSeek 回复: " + response);
    
    // 多轮对话
    List<AiMessage> messages = Arrays.asList(
        new AiMessage("user", "请帮我写一个Java排序算法"),
        new AiMessage("assistant", "好的，我来为你写一个快速排序算法。"),
        new AiMessage("user", "能解释一下时间复杂度吗？")
    );
    String multiResponse = deepseekService.chat(messages);
    System.out.println("多轮对话回复: " + multiResponse);
    
    // 流式对话
    deepseekService.chatStream("请解释什么是深度学习", content -> {
        System.out.print(content);
    });
}
```

## 🎯 支持的模型

DeepSeek 提供商支持以下模型：

### 主要模型
- `deepseek-chat` - DeepSeek V3 最新一代对话模型（推荐）
- `deepseek-reasoner` - DeepSeek R1 推理模型，擅长数学和逻辑推理

### 历史版本
- `deepseek-v3` - DeepSeek V3 基础版本
- `deepseek-v3-0324` - DeepSeek V3 特定版本
- `deepseek-v2.5` - DeepSeek V2.5 版本
- `deepseek-v2.5-1210` - DeepSeek V2.5 特定版本
- `deepseek-r1` - DeepSeek R1 基础版本
- `deepseek-r1-lite` - DeepSeek R1 轻量版

### 专用模型
- `deepseek-coder` - 代码生成专用模型
- `deepseek-coder-v2-instruct` - 代码指令微调模型
- `deepseek-coder-v2-lite-instruct` - 代码轻量指令模型
- `deepseek-math` - 数学专用模型

## ⚙️ 详细配置

### 完整配置示例

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: deepseek
    timeout: 30000
    read-timeout: 60000
    providers:
      deepseek:
        api-key: sk-your-deepseek-api-key-here
        api-url: https://api.deepseek.com  # 可选，默认值
        default-model: deepseek-chat
        parameters:
          # 基础参数
          temperature: 1.0          # 0.0-2.0，控制随机性
          max_tokens: 4096          # 最大输出token数
          top_p: 1.0               # 0.0-1.0，核心采样
          
          # 惩罚参数
          frequency_penalty: 0.0    # -2.0到2.0，频率惩罚
          presence_penalty: 0.0     # -2.0到2.0，存在惩罚
          
          # 停止词
          stop: ["Human:", "AI:", "###"]
          
          # DeepSeek特有功能
          logprobs: true           # 返回对数概率
          top_logprobs: 5          # 返回前N个概率最高的token
          
          # JSON输出格式
          response_format:
            type: "json_object"    # 强制JSON输出
```

### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `temperature` | Number | 1.0 | 控制输出的随机性，0最确定，2最随机 |
| `max_tokens` | Integer | 4096 | 生成的最大token数量 |
| `top_p` | Number | 1.0 | 核心采样参数，建议与temperature二选一 |
| `frequency_penalty` | Number | 0.0 | 基于频率的惩罚，减少重复 |
| `presence_penalty` | Number | 0.0 | 基于存在性的惩罚，鼓励新话题 |
| `stop` | Array | null | 停止生成的字符串列表 |
| `logprobs` | Boolean | false | 是否返回对数概率信息 |
| `top_logprobs` | Integer | null | 返回前N个概率最高的token |
| `response_format` | Object | null | 响应格式，支持JSON输出 |

## 🔧 高级功能

### 1. 推理模型使用

DeepSeek R1 推理模型特别适合需要复杂推理的任务：

```yaml
bcl:
  ai-framework:
    providers:
      deepseek:
        api-key: sk-your-api-key
        default-model: deepseek-reasoner  # 使用推理模型
        parameters:
          temperature: 0.1               # 推理任务建议使用低温度
          max_tokens: 8192              # 推理可能需要更多token
```

### 2. JSON 输出模式

强制模型以JSON格式输出：

```yaml
bcl:
  ai-framework:
    providers:
      deepseek:
        parameters:
          response_format:
            type: "json_object"
```

```java
// 使用示例
String prompt = "请以JSON格式返回以下信息：姓名、年龄、职业";
String jsonResponse = deepseekService.chat(prompt);
```

### 3. 代码生成优化

使用专门的代码模型：

```yaml
bcl:
  ai-framework:
    providers:
      deepseek:
        default-model: deepseek-coder-v2-instruct
        parameters:
          temperature: 0.2    # 代码生成建议低温度
          max_tokens: 4096
```

### 4. 对数概率分析

获取模型的置信度信息：

```yaml
bcl:
  ai-framework:
    providers:
      deepseek:
        parameters:
          logprobs: true
          top_logprobs: 10    # 返回前10个最可能的token
```

## 📊 模型选择策略

### 1. 按用途选择模型

| 用途 | 推荐模型 | 特点 |
|------|----------|------|
| 日常对话 | deepseek-chat | 通用性强，响应自然 |
| 数学推理 | deepseek-reasoner | 逻辑推理能力强 |
| 代码生成 | deepseek-coder | 专门优化代码任务 |
| 数学问题 | deepseek-math | 数学专业能力 |

### 2. 性能对比

```java
public void compareModels() {
    AiServiceFactory factory = // 获取factory
    
    // 获取支持的模型
    List<String> models = factory.getSupportedModels("deepseek");
    System.out.println("支持的模型: " + models);
    
    // 不同模型的适用场景
    // deepseek-chat: 通用对话，平衡性能
    // deepseek-reasoner: 复杂推理，数学解题
    // deepseek-coder: 代码生成，编程助手
}
```

## 🚨 常见问题

### 1. API Key 无效
```
错误: DeepSeek API 错误 [invalid_api_key]: Incorrect API key provided
解决: 检查API Key是否正确，是否有权限
```

### 2. 模型不支持
```
错误: DeepSeek API 错误 [model_not_found]: The model does not exist
解决: 检查模型名称是否正确，账户是否有权限使用该模型
```

### 3. 上下文长度超限
```
错误: DeepSeek API 错误 [context_length_exceeded]: Context length exceeded
解决: 减少输入长度或使用更大上下文的模型
```

### 4. 请求频率限制
```yaml
# 增加超时时间和重试间隔
bcl:
  ai-framework:
    timeout: 60000      # 连接超时60秒
    read-timeout: 120000 # 读取超时120秒
```

## 🔍 监控和调试

### 1. 开启详细日志

```yaml
logging:
  level:
    com.chow.easy.ai.framework.provider.deepseek: DEBUG
```

### 2. 推理过程查看

对于推理模型，可以在日志中查看推理过程：

```java
// 推理过程会在DEBUG级别日志中显示
// 格式：DeepSeek 推理过程: [具体推理步骤]
```

### 3. 性能监控

```java
public void monitorUsage() {
    long startTime = System.currentTimeMillis();
    
    String response = deepseekService.chat("测试消息");
    
    long duration = System.currentTimeMillis() - startTime;
    System.out.println("请求耗时: " + duration + "ms");
    System.out.println("响应长度: " + response.length());
}
```

## 💡 最佳实践

### 1. 温度设置建议
- **创意写作**: temperature = 1.2-1.5
- **日常对话**: temperature = 0.7-1.0  
- **代码生成**: temperature = 0.1-0.3
- **数学推理**: temperature = 0.0-0.2

### 2. 模型切换策略
```java
public String intelligentChat(String message) {
    // 根据消息内容智能选择模型
    if (message.contains("代码") || message.contains("编程")) {
        return serviceFactory.getService("deepseek-coder").chat(message);
    } else if (message.contains("数学") || message.contains("计算")) {
        return serviceFactory.getService("deepseek-reasoner").chat(message);
    } else {
        return serviceFactory.getService("deepseek-chat").chat(message);
    }
}
```

### 3. 成本优化
- 使用合适的max_tokens限制
- 对于简单任务使用轻量模型
- 合理设置temperature避免重复生成

### 4. 错误处理
```java
public String robustChat(String message) {
    try {
        return deepseekService.chat(message);
    } catch (RuntimeException e) {
        if (e.getMessage().contains("rate_limit")) {
            // 实现重试逻辑
            Thread.sleep(1000);
            return deepseekService.chat(message);
        }
        throw e;
    }
}
```

## 🔗 参考链接

- [DeepSeek 官方文档](https://api-docs.deepseek.com/)
- [DeepSeek 平台](https://platform.deepseek.com/)
- [BCL AI Framework API 文档](./README.md)
- [SPI 机制使用指南](./SPI-GUIDE.md)

通过本指南，你现在可以充分利用 DeepSeek 在 BCL AI Framework 中的强大推理和代码生成能力！🚀 