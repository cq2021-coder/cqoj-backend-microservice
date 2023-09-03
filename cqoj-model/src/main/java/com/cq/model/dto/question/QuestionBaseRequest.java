package com.cq.model.dto.question;

import lombok.Data;

import java.util.List;

/**
 * 题目基本请求
 *
 * @author 程崎
 * @since 2023/08/08
 */
@Data
public class QuestionBaseRequest {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;
}
