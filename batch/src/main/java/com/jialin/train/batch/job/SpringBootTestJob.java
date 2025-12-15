//package com.jialin.train.batch.job;
//
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///*
// * 适合单体应用，不适合集群
// */
//@Component
//@EnableScheduling
//public class SpringBootTestJob {
//
//    @Scheduled(cron = "0/5 * * * * ?")
//    private void test() {
//        // 可以通过增加分布式锁解决集群问题
//        System.out.println("SpringBootTestJob test");
//    }
//}
