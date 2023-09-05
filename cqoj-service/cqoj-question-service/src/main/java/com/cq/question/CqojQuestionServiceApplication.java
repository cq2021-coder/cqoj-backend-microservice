package com.cq.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.cq")
@EnableFeignClients(basePackages = "com.cq.client.feign")
public class CqojQuestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CqojQuestionServiceApplication.class, args);
	}

}
