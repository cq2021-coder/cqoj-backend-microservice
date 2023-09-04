package com.cq.client.feign;


import com.cq.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 判题服务
 *
 * @author 程崎
 * @since 2023/09/03
 */
@FeignClient(name = "cqoj-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 判题
     *
     * @param questionSubmit 问题提交
     */
    @PutMapping("/do")
    void doJudge(@RequestBody QuestionSubmit questionSubmit);
}
