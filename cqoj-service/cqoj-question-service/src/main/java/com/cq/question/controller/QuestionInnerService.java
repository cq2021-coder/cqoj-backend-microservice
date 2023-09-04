package com.cq.question.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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

    @PostMapping("/get/wrapper")
    public Question getOne(Wrapper<Question> queryWrapper) {
        return questionService.getOne(queryWrapper);
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
