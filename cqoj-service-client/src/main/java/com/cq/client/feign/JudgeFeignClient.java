package com.cq.client.feign;


import com.cq.model.entity.QuestionSubmit;

/**
 * 判题服务
 *
 * @author 程崎
 * @since 2023/09/03
 */
public interface JudgeFeignClient {

    void doJudge(QuestionSubmit questionSubmit);
}
