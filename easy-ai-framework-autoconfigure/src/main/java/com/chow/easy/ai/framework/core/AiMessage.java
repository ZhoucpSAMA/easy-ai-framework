package com.chow.easy.ai.framework.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author chowsama
 * @date 2025/06/26 09:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AiMessage {
    //角色 注意：如果设置系统消息，请放在messages列表的第一位
    private String role;
    //内容
    private String content;
}
