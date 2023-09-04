package com.cq.judge.controller;

import com.cq.judge.service.JudgeService;
import com.cq.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController {
    @Resource
    private JudgeService judgeService;

    @PutMapping("/do")
    public void doJudge(@RequestBody QuestionSubmit questionSubmit) {
        judgeService.doJudge(questionSubmit);
    }
}
