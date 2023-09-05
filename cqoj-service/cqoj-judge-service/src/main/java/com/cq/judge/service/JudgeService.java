package com.cq.judge.service;


import com.cq.model.entity.QuestionSubmit;
import com.cq.model.vo.QuestionSubmitVO;

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
     * @return {@link QuestionSubmitVO}
     */
    QuestionSubmit doJudge(QuestionSubmit questionSubmit);
}
