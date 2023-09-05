package com.cq.judge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.cq")
@EnableFeignClients(basePackages = "com.cq.client.feign")
public class CqojJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqojJudgeServiceApplication.class, args);
    }

}
