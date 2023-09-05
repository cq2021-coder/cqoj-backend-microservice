package com.cq.question.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cq.model.entity.Question;
import com.cq.model.entity.QuestionSubmit;
import com.cq.question.service.QuestionService;
import com.cq.question.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionInnerService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    public Question getOne(Long questionId) {
        return questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getId, questionId).select(Question::getJudgeCase, Question::getJudgeConfig)
        );
    }

    @PostMapping("/question-submit/update")
    public void updateById(@RequestBody QuestionSubmit questionSubmitUpdate) {
        questionSubmitService.updateById(questionSubmitUpdate);
    }

    @GetMapping("/question-submit/get/id")
    public QuestionSubmit selectById(Long id) {
        return questionSubmitService.getById(id);
    }
}
