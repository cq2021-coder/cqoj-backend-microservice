package com.cq.client.feign;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.cq.model.entity.Question;
import com.cq.model.entity.QuestionSubmit;

/**
 * 题目服务
 *
 * @author 程崎
 * @since 2023/09/03
 */
public interface QuestionFeignClient {
    Question getOne(Wrapper<Question> queryWrapper);

    void updateById(QuestionSubmit questionSubmitUpdate);

    QuestionSubmit selectById(Long id);
}
