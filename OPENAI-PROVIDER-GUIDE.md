# BCL AI Framework - OpenAI 提供商使用指南

本文档介绍如何在 BCL AI Framework 中使用 OpenAI 提供商。

## 🚀 快速开始

### 1. 配置 OpenAI API Key

在 `application.yml` 中配置：

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: openai
    providers:
      openai:
        api-key: sk-your-openai-api-key-here
        default-model: gpt-3.5-turbo
        parameters:
          temperature: 0.7
          max_tokens: 2000
```

### 2. 获取 API Key

1. 访问 [OpenAI Platform](https://platform.openai.com/)
2. 注册或登录账户
3. 在 [API Keys](https://platform.openai.com/api-keys) 页面创建新的 API Key
4. 将 API Key 配置到项目中

### 3. 基本使用

```java
@Autowired
private AiServiceFactory serviceFactory;

public void useOpenAI() {
    AiService openaiService = serviceFactory.getService("openai");
    
    // 简单对话
    String response = openaiService.chat("你好，请介绍一下你自己");
    System.out.println("OpenAI 回复: " + response);
    
    // 多轮对话
    List<AiMessage> messages = Arrays.asList(
        new AiMessage("user", "我正在学习Java编程"),
        new AiMessage("assistant", "很好！Java是一门强大的编程语言。"),
        new AiMessage("user", "能给我一个简单的例子吗？")
    );
    String multiResponse = openaiService.chat(messages);
    System.out.println("多轮对话回复: " + multiResponse);
    
    // 流式对话
    openaiService.chatStream("请解释什么是机器学习", content -> {
        System.out.print(content);
    });
}
```

## 🎯 支持的模型

OpenAI 提供商支持以下模型：

### GPT-4 系列
- `gpt-4` - GPT-4 基础模型
- `gpt-4-turbo` - GPT-4 Turbo，速度更快
- `gpt-4-turbo-preview` - GPT-4 Turbo 预览版
- `gpt-4-1106-preview` - GPT-4 特定版本
- `gpt-4-0125-preview` - GPT-4 2024年1月版本
- `gpt-4-vision-preview` - 支持图像理解的GPT-4

### GPT-3.5 系列
- `gpt-3.5-turbo` - GPT-3.5 Turbo（推荐）
- `gpt-3.5-turbo-16k` - 支持16K上下文
- `gpt-3.5-turbo-1106` - GPT-3.5 特定版本
- `gpt-3.5-turbo-0125` - GPT-3.5 2024年1月版本

### 最新模型
- `gpt-4o` - GPT-4o 多模态模型
- `gpt-4o-mini` - GPT-4o 轻量版
- `chatgpt-4o-latest` - 最新的ChatGPT模型

### O1 系列（推理模型）
- `o1-preview` - O1 推理模型预览版
- `o1-mini` - O1 轻量推理模型

## ⚙️ 详细配置

### 完整配置示例

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: openai
    timeout: 30000
    read-timeout: 60000
    providers:
      openai:
        api-key: sk-your-openai-api-key-here
        api-url: https://api.openai.com/v1  # 可选，默认值
        default-model: gpt-4-turbo
        parameters:
          # 基础参数
          temperature: 0.7          # 0.0-2.0，控制随机性
          max_tokens: 2000          # 最大输出token数
          top_p: 1.0               # 0.0-1.0，核心采样
          
          # 惩罚参数
          frequency_penalty: 0.0    # -2.0到2.0，频率惩罚
          presence_penalty: 0.0     # -2.0到2.0，存在惩罚
          
          # 高级参数
          stop: ["Human:", "AI:"]   # 停止词列表
          n: 1                     # 生成数量
          user: "test-user"        # 用户标识
          
          # logit_bias 示例（可选）
          logit_bias:
            "50256": -100          # 阻止特定token
```

### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `temperature` | Number | 0.7 | 控制输出的随机性，0最确定，2最随机 |
| `max_tokens` | Integer | 2000 | 生成的最大token数量 |
| `top_p` | Number | 1.0 | 核心采样参数，建议与temperature二选一 |
| `frequency_penalty` | Number | 0.0 | 基于频率的惩罚，减少重复 |
| `presence_penalty` | Number | 0.0 | 基于存在性的惩罚，鼓励新话题 |
| `stop` | Array | null | 停止生成的字符串列表 |
| `n` | Integer | 1 | 每次请求生成的回复数量 |
| `user` | String | null | 用户标识，用于监控和滥用检测 |

## 🔧 高级功能

### 1. 代理配置

如果需要通过代理访问 OpenAI API：

```yaml
bcl:
  ai-framework:
    providers:
      openai:
        api-key: sk-your-api-key
        api-url: https://your-proxy.com/v1  # 代理地址
```

### 2. Azure OpenAI 配置

使用 Azure OpenAI Service：

```yaml
bcl:
  ai-framework:
    providers:
      openai:
        api-key: your-azure-openai-key
        api-url: https://your-resource.openai.azure.com/openai/deployments/your-deployment
        default-model: gpt-35-turbo  # Azure部署名称
```

### 3. 自定义参数

```java
// 动态设置参数
Map<String, Object> customParams = new HashMap<>();
customParams.put("temperature", 0.9);
customParams.put("max_tokens", 1000);

// 通过配置更新（需要重启服务）
```

### 4. 模型选择策略

```java
public void useOptimalModel() {
    AiServiceFactory factory = // 获取factory
    
    // 获取支持的模型
    List<String> models = factory.getSupportedModels("openai");
    System.out.println("支持的模型: " + models);
    
    // 根据需求选择模型
    // gpt-3.5-turbo: 性价比高，适合一般对话
    // gpt-4: 质量最高，适合复杂任务
    // gpt-4-turbo: 平衡性能和质量
}
```

## 📊 费用优化

### 1. 选择合适的模型

| 模型 | 输入价格 | 输出价格 | 适用场景 |
|------|----------|----------|----------|
| gpt-3.5-turbo | $0.0005/1K tokens | $0.0015/1K tokens | 日常对话，简单任务 |
| gpt-4-turbo | $0.01/1K tokens | $0.03/1K tokens | 复杂推理，高质量需求 |
| gpt-4o-mini | $0.00015/1K tokens | $0.0006/1K tokens | 成本敏感的应用 |

### 2. 优化配置

```yaml
# 成本优化配置
bcl:
  ai-framework:
    providers:
      openai:
        default-model: gpt-3.5-turbo  # 使用性价比高的模型
        parameters:
          max_tokens: 500             # 限制输出长度
          temperature: 0.3            # 降低随机性，提高一致性
```

## 🚨 常见问题

### 1. API Key 无效
```
错误: OpenAI API 错误 [invalid_api_key]: Incorrect API key provided
解决: 检查API Key是否正确，是否有权限
```

### 2. 配额限制
```
错误: OpenAI API 错误 [insufficient_quota]: You exceeded your current quota
解决: 检查账户余额和使用配额
```

### 3. 模型不存在
```
错误: OpenAI API 错误 [model_not_found]: The model does not exist
解决: 检查模型名称是否正确，账户是否有权限使用该模型
```

### 4. 请求超时
```yaml
# 增加超时时间
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
    com.xkw.bcl.ai.framework.provider.openai: DEBUG
```

### 2. 查看请求详情

日志中会显示：
- API请求URL和参数
- 响应状态和内容
- 错误详情和堆栈

### 3. 性能监控

```java
public void monitorUsage() {
    long startTime = System.currentTimeMillis();
    
    String response = openaiService.chat("测试消息");
    
    long duration = System.currentTimeMillis() - startTime;
    System.out.println("请求耗时: " + duration + "ms");
    System.out.println("响应长度: " + response.length());
}
```

## 🎯 最佳实践

1. **选择合适的模型**: 根据任务复杂度选择性价比最优的模型
2. **设置合理的参数**: 避免过高的temperature和max_tokens
3. **实现重试机制**: 处理网络异常和API限流
4. **缓存常用结果**: 避免重复请求相同内容
5. **监控使用量**: 定期检查API使用情况和费用

通过本指南，你现在可以充分利用 OpenAI 在 BCL AI Framework 中的强大功能！🚀 