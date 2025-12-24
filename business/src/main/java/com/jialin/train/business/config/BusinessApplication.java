package com.jialin.train.business.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan("com.jialin")
@MapperScan("com.jialin.train.*.mapper")
@EnableFeignClients("com.jialin.train.business.feign")
public class BusinessApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("Launched successfully!");
        LOG.info("URL: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
