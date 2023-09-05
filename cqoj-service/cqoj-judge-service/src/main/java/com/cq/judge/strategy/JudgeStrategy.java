package com.cq.judge.strategy;


import com.cq.model.dto.questionsubmit.JudgeInfo;

/**
 * 判题策略
 *
 * @author 程崎
 * @since 2023/08/16
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     *
     * @param judgeContext 判题上下文
     * @return {@link JudgeInfo}
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
