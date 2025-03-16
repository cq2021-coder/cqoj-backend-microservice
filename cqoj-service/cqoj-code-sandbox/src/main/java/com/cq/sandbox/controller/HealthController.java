package com.cq.sandbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康控制器
 *
 * @author 程崎
 * @since 2023/08/19
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/check")
    public String check() {
        return "ok";
    }

}
