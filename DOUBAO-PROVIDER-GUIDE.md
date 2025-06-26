# BCL AI Framework - Doubao（豆包）提供商使用指南

本文档介绍如何在 BCL AI Framework 中使用 Doubao（豆包）提供商。

## 🚀 快速开始

### 1. 配置 Doubao API Key

在 `application.yml` 中配置：

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: doubao
    providers:
      doubao:
        api-key: your-doubao-api-key-here
        default-model: doubao-seed-1.6
        parameters:
          temperature: 0.7
          max_tokens: 4096
```

### 2. 获取 API Key

1. 访问 [火山方舟大模型服务平台](https://www.volcengine.com/product/doubao)
2. 注册并登录账户，完成实名认证
3. 在 API Key 管理页面创建新的 API Key
4. 如需使用特定模型，需要先在开通管理页面开通对应模型服务
5. 可选：创建推理接入点获得更高级的功能（限流、监控等）

### 3. 基本使用

```java
@Autowired
private AiServiceFactory serviceFactory;

public void useDoubao() {
    AiService doubaoService = serviceFactory.getService("doubao");
    
    // 简单对话
    String response = doubaoService.chat("你好，请介绍一下豆包");
    System.out.println("Doubao 回复: " + response);
    
    // 多轮对话
    List<AiMessage> messages = Arrays.asList(
        new AiMessage("user", "请帮我写一个Java排序算法"),
        new AiMessage("assistant", "好的，我来为你写一个快速排序算法。"),
        new AiMessage("user", "能优化一下性能吗？")
    );
    String multiResponse = doubaoService.chat(messages);
    System.out.println("多轮对话回复: " + multiResponse);
    
    // 流式对话
    doubaoService.chatStream("请解释什么是深度学习", content -> {
        System.out.print(content);
    });
}
```

## 🎯 支持的模型

Doubao 提供商支持以下模型：

### 最新推荐模型
- `doubao-seed-1.6` - 全新多模态深度思考模型（推荐）
- `doubao-seed-1.6-flash` - 极致推理速度的多模态模型  
- `doubao-seed-1.6-thinking` - 强化思考能力的模型

### 主流模型
- `doubao-1.5-pro-32k` - Pro版本，32k上下文
- `doubao-1.5-pro-256k` - Pro版本，256k上下文
- `doubao-1.5-lite` - Lite版本，高性价比
- `doubao-1.5-thinking-pro` - 深度思考Pro版本

### 多模态模型
- `doubao-1.5-vision-pro` - 视觉理解Pro版本
- `doubao-1.5-vision-lite` - 视觉理解Lite版本
- `doubao-vision-pro` - 视觉理解模型
- `doubao-vision-lite` - 视觉理解轻量版

### 专用模型
- `doubao-1.5-ui-tars` - UI任务处理模型
- `doubao-seedance-1.0-lite` - 视频生成Lite版
- `doubao-seedance-1.0-pro` - 视频生成Pro版
- `doubao-seedream-3.0-t2i` - 图片生成模型

### 向量化模型
- `doubao-embedding` - 文本向量化
- `doubao-embedding-large` - 大型向量化模型
- `doubao-embedding-vision` - 图文向量化模型

## ⚙️ 详细配置

### 完整配置示例

```yaml
bcl:
  ai-framework:
    enabled: true
    default-provider: doubao
    timeout: 30000
    read-timeout: 60000
    providers:
      doubao:
        api-key: your-doubao-api-key-here
        api-url: https://ark.cn-beijing.volces.com/api/v3  # 可选，默认值
        default-model: doubao-seed-1.6
        parameters:
          # 基础参数
          temperature: 0.7          # 0.0-2.0，控制随机性
          max_tokens: 4096          # 最大输出token数
          top_p: 1.0               # 0.0-1.0，核心采样
          
          # 惩罚参数
          frequency_penalty: 0.0    # -2.0到2.0，频率惩罚
          presence_penalty: 0.0     # -2.0到2.0，存在惩罚
          
          # 停止词
          stop: ["Human:", "AI:", "###"]
          
          # 豆包特有功能
          endpoint_id: ep-xxxxxxxx-xxxxx  # 自定义接入点ID
          
          # Function Calling（工具调用）
          functions: []
          function_call: "auto"
          tools: []
          tool_choice: "auto"
```

### 参数说明

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `temperature` | Number | 0.7 | 控制输出的随机性，0最确定，2最随机 |
| `max_tokens` | Integer | 4096 | 生成的最大token数量 |
| `top_p` | Number | 1.0 | 核心采样参数，建议与temperature二选一 |
| `frequency_penalty` | Number | 0.0 | 基于频率的惩罚，减少重复 |
| `presence_penalty` | Number | 0.0 | 基于存在性的惩罚，鼓励新话题 |
| `stop` | Array | null | 停止生成的字符串列表 |
| `endpoint_id` | String | null | 自定义接入点ID，优先级高于模型名称 |
| `functions` | Array | null | Function Calling函数定义 |
| `function_call` | String/Object | null | Function Calling调用方式 |
| `tools` | Array | null | 工具调用定义 |
| `tool_choice` | String/Object | null | 工具选择策略 |

## 🔧 高级功能

### 1. 自定义接入点使用

火山方舟支持创建自定义推理接入点，提供更好的控制能力：

```yaml
bcl:
  ai-framework:
    providers:
      doubao:
        api-key: your-api-key
        # 使用接入点ID而非模型名称
        default-model: ep-20241202001-xxxxx
        # 或在参数中指定
        parameters:
          endpoint_id: ep-20241202001-xxxxx
```

### 2. 多模态功能

使用视觉理解模型：

```yaml
bcl:
  ai-framework:
    providers:
      doubao:
        default-model: doubao-1.5-vision-pro
        parameters:
          temperature: 0.2    # 视觉任务建议低温度
          max_tokens: 2048
```

### 3. Function Calling

豆包支持函数调用功能：

```yaml
bcl:
  ai-framework:
    providers:
      doubao:
        parameters:
          functions:
            - name: "get_weather"
              description: "获取指定城市的天气信息"
              parameters:
                type: "object"
                properties:
                  city:
                    type: "string"
                    description: "城市名称"
                required: ["city"]
          function_call: "auto"
```

### 4. 深度思考模式

使用思考能力强化的模型：

```yaml
bcl:
  ai-framework:
    providers:
      doubao:
        default-model: doubao-seed-1.6-thinking
        parameters:
          temperature: 0.1    # 思考任务建议低温度
          max_tokens: 8192    # 思考可能需要更多token
```

## 📊 模型选择策略

### 1. 按用途选择模型

| 用途 | 推荐模型 | 特点 |
|------|----------|------|
| 日常对话 | doubao-seed-1.6 | 综合能力强，多模态支持 |
| 快速响应 | doubao-seed-1.6-flash | 推理速度快，性价比高 |
| 复杂推理 | doubao-seed-1.6-thinking | 思考能力强化 |
| 视觉理解 | doubao-1.5-vision-pro | 图像理解专业 |
| 代码生成 | doubao-1.5-pro-32k | 代码能力强 |
| 成本优化 | doubao-1.5-lite | 高性价比选择 |

### 2. 性能对比

```java
public void compareModels() {
    AiServiceFactory factory = // 获取factory
    
    // 获取支持的模型
    List<String> models = factory.getSupportedModels("doubao");
    System.out.println("支持的模型: " + models);
    
    // 不同模型的适用场景
    // doubao-seed-1.6: 最新一代，多模态，思考能力强
    // doubao-seed-1.6-flash: 极致速度，成本优化
    // doubao-1.5-vision-pro: 视觉理解专业版
}
```

## 🚨 常见问题

### 1. API Key 无效
```
错误: Doubao API 错误 [invalid_api_key]: API key invalid
解决: 检查API Key是否正确，是否从火山方舟平台获取
```

### 2. 模型未开通
```
错误: Doubao API 错误 [model_not_found]: Model not accessible
解决: 在火山方舟控制台的开通管理页面开通相应模型
```

### 3. 接入点不存在
```
错误: Doubao API 错误 [endpoint_not_found]: Endpoint not found
解决: 检查接入点ID是否正确，或在控制台创建推理接入点
```

### 4. 请求频率限制
```yaml
# 使用自定义接入点可以获得更高的限制
bcl:
  ai-framework:
    timeout: 60000      # 增加超时时间
    read-timeout: 120000 # 增加读取超时
    providers:
      doubao:
        default-model: ep-your-endpoint-id  # 使用自定义接入点
```

## 🔍 监控和调试

### 1. 开启详细日志

```yaml
logging:
  level:
    com.xkw.bcl.ai.framework.provider.doubao: DEBUG
```

### 2. 性能监控

```java
public void monitorUsage() {
    long startTime = System.currentTimeMillis();
    
    String response = doubaoService.chat("测试消息");
    
    long duration = System.currentTimeMillis() - startTime;
    System.out.println("请求耗时: " + duration + "ms");
    System.out.println("响应长度: " + response.length());
}
```

### 3. 错误处理

```java
public String robustChat(String message) {
    try {
        return doubaoService.chat(message);
    } catch (RuntimeException e) {
        if (e.getMessage().contains("rate_limit")) {
            // 实现重试逻辑
            Thread.sleep(1000);
            return doubaoService.chat(message);
        }
        throw e;
    }
}
```

## 💡 最佳实践

### 1. 温度设置建议
- **创意写作**: temperature = 1.0-1.5
- **日常对话**: temperature = 0.7-1.0  
- **代码生成**: temperature = 0.1-0.3
- **逻辑推理**: temperature = 0.0-0.2

### 2. 模型切换策略
```java
public String intelligentChat(String message) {
    // 根据消息内容智能选择模型
    if (message.contains("图片") || message.contains("图像")) {
        return serviceFactory.getService("doubao-vision").chat(message);
    } else if (message.contains("思考") || message.contains("推理")) {
        return serviceFactory.getService("doubao-thinking").chat(message);
    } else {
        return serviceFactory.getService("doubao").chat(message);
    }
}
```

### 3. 成本优化
- 使用合适的max_tokens限制
- 对于简单任务使用Lite模型
- 合理设置temperature避免重复生成
- 考虑使用自定义接入点获得更好的价格

### 4. 安全考虑
- API Key 存储在环境变量中
- 启用请求日志审计
- 设置合理的超时时间
- 实现重试和降级机制

## 🔗 参考链接

- [火山方舟平台](https://www.volcengine.com/product/doubao)
- [豆包官方文档](https://www.volcengine.com/docs/82379/1099455)
- [API 参考文档](https://www.volcengine.com/docs/82379/1099463)
- [BCL AI Framework API 文档](./README.md)
- [SPI 机制使用指南](./SPI-GUIDE.md)

通过本指南，你现在可以充分利用豆包在 BCL AI Framework 中的强大多模态和深度思考能力！🚀 