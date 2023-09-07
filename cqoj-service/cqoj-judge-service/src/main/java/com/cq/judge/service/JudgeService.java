package com.cq.judge.service;


import com.cq.model.entity.QuestionSubmit;

/**
 * 判题服务
 *
 * @author 程崎
 * @since 2023/08/15
 */
public interface JudgeService {
    /**
     * 判题
     *
     * @param questionSubmit 提交题目数据
     */
    void doJudge(QuestionSubmit questionSubmit);
}
