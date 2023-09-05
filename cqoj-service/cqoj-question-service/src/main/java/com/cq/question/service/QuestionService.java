package com.cq.question.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.model.dto.question.QuestionQueryRequest;
import com.cq.model.entity.Question;
import com.cq.model.vo.QuestionManageVO;
import com.cq.model.vo.QuestionVO;

/**
 * 题目服务
 *
 * @author 程崎
 * @since 2023/09/03
 */
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question 题目
     * @param add      添加
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest 题目查询请求
     * @return {@link QueryWrapper}<{@link Question}>
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question 题目
     * @return {@link QuestionVO}
     */
    QuestionVO getQuestionVO(Question question);

    /**
     * 分页获取题目封装
     *
     * @param questionPage 题目页面
     * @return {@link Page}<{@link QuestionVO}>
     */
    Page<QuestionVO> getQuestionVoPage(Page<Question> questionPage);

    /**
     * 管理题目列表页面
     *
     * @param questionPage   题目分页
     * @param queryWrapper 查询条件
     * @return {@link Page}<{@link QuestionManageVO}>
     */
    Page<QuestionManageVO> listManageQuestionByPage(Page<Question> questionPage, QueryWrapper<Question> queryWrapper);
}
