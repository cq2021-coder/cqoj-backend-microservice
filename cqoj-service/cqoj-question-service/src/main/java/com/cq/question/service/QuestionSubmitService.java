package com.cq.question.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cq.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cq.model.entity.QuestionSubmit;
import com.cq.model.entity.User;
import com.cq.model.vo.QuestionSubmitVO;
import com.cq.model.vo.QuestionSubmitViewVO;

/**
 * 题目提交服务
 *
 * @author 程崎
 * @since 2023/09/03
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser                登录用户
     * @return long
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest 问题提交查询请求
     * @return {@link QueryWrapper}<{@link QuestionSubmit}>
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit 问题提交
     * @param loginUser      登录用户
     * @return {@link QuestionSubmitVO}
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage 问题提交页面
     * @param loginUser          登录用户
     * @return {@link Page}<{@link QuestionSubmitVO}>
     */
    Page<QuestionSubmitVO> getQuestionSubmitVoPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);


    Page<QuestionSubmitViewVO> listQuestionSubmitByPage(String title, String language, long pageIndex, long size);
}
