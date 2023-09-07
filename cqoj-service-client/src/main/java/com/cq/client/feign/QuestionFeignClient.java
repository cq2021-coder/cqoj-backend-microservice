package com.cq.client.feign;


import com.cq.model.entity.Question;
import com.cq.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 题目服务
 *
 * @author 程崎
 * @since 2023/09/03
 */
@FeignClient(name = "cqoj-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getOne(@RequestParam("questionId") Long questionId);

    @PostMapping("/update/id")
    void updateQuestion(@RequestBody Question question);

    @PostMapping("/question-submit/update")
    void updateById(@RequestBody QuestionSubmit questionSubmitUpdate);

    @GetMapping("/question-submit/get/id")
    QuestionSubmit selectById(@RequestParam("id") Long id);
}
