package com.jialin.train.batch.controller;

import com.jialin.train.batch.req.CronJobReq;
import com.jialin.train.batch.resp.CronJobResp;
import com.jialin.train.common.resp.CommonResp;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "/admin/job")
public class JobController {

    private static Logger LOG = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @RequestMapping(value = "/run")
    public CommonResp<Object> run(@RequestBody CronJobReq cronJobReq) throws SchedulerException {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("Manually executing job started: {}, {}", jobClassName, jobGroupName);
        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
        return new CommonResp<>();
    }

    @RequestMapping(value = "/add")
    public CommonResp add(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LOG.info("Creating scheduled job started: {}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        CommonResp commonResp = new CommonResp();

        try {
            // Get a Scheduler instance via SchedulerFactory
            Scheduler sched = schedulerFactoryBean.getScheduler();

            // Start the scheduler
            sched.start();

            // Build job information
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName)).withIdentity(jobClassName, jobGroupName).build();

            // Expression scheduler builder (i.e., the job execution time)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            // Build a new trigger with the new cronExpression
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withDescription(description).withSchedule(scheduleBuilder).build();

            sched.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            LOG.error("Failed to create scheduled job: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to create scheduled job: Scheduling exception");
        } catch (ClassNotFoundException e) {
            LOG.error("Failed to create scheduled job: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to create scheduled job: Job class not found");
        }
        LOG.info("Creating scheduled job finished: {}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/pause")
    public CommonResp pause(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("Pausing scheduled job started: {}，{}", jobClassName, jobGroupName);
        CommonResp commonResp = new CommonResp();
        try {
            Scheduler sched = schedulerFactoryBean.getScheduler();
            sched.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("Failed to pause scheduled job: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to pause scheduled job: Scheduling exception");
        }
        LOG.info("Pausing scheduled job finished: {}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/resume")
    public CommonResp resume(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("Resuming scheduled job started: {}，{}", jobClassName, jobGroupName);
        CommonResp commonResp = new CommonResp();
        try {
            Scheduler sched = schedulerFactoryBean.getScheduler();
            sched.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("Failed to resume scheduled job: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to resume scheduled job: Scheduling exception");
        }
        LOG.info("Resuming scheduled job finished: {}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/reschedule")
    public CommonResp reschedule(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LOG.info("Rescheduling (Updating) job started: {}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        CommonResp commonResp = new CommonResp();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // Expression schedule builder
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            trigger1.setStartTime(new Date()); // Reset start time
            CronTrigger trigger = trigger1;

            // Rebuild trigger with the new cronExpression
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(description).withSchedule(scheduleBuilder).build();

            // Reschedule job with the new trigger
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            LOG.error("Failed to reschedule job: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to reschedule job: Scheduling exception");
        }
        LOG.info("Rescheduling job finished: {}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/delete")
    public CommonResp delete(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("Deleting scheduled job started: {}，{}", jobClassName, jobGroupName);
        CommonResp commonResp = new CommonResp();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            // Pause the trigger
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            // Unschedule (remove) the trigger
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            // Delete the Job
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("Failed to delete scheduled job: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to delete scheduled job: Scheduling exception");
        }
        LOG.info("Deleting scheduled job finished: {}", commonResp);
        return commonResp;
    }

    @RequestMapping(value="/query")
    public CommonResp query() {
        LOG.info("Querying all scheduled jobs started");
        CommonResp commonResp = new CommonResp();
        List<CronJobResp> cronJobDtoList = new ArrayList();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    CronJobResp cronJobResp = new CronJobResp();
                    cronJobResp.setName(jobKey.getName());
                    cronJobResp.setGroup(jobKey.getGroup());

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                    cronJobResp.setNextFireTime(cronTrigger.getNextFireTime());
                    cronJobResp.setPreFireTime(cronTrigger.getPreviousFireTime());
                    cronJobResp.setCronExpression(cronTrigger.getCronExpression());
                    cronJobResp.setDescription(cronTrigger.getDescription());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());
                    cronJobResp.setState(triggerState.name());

                    cronJobDtoList.add(cronJobResp);
                }

            }
        } catch (SchedulerException e) {
            LOG.error("Failed to query scheduled jobs: " + e);
            commonResp.setSuccess(false);
            commonResp.setMessage("Failed to query scheduled jobs: Scheduling exception");
        }
        commonResp.setContent(cronJobDtoList);
        LOG.info("Querying all scheduled jobs finished: {}", commonResp);
        return commonResp;
    }

}