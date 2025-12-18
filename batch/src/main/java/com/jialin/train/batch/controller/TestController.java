package com.jialin.train.batch.controller;

import com.jialin.train.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Resource
    BusinessFeign businessFeign;

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public String hello() {
        String businessHello = businessFeign.hello();
        LOG.info(businessHello);
        return "hello world! Batch!";
    }
}
