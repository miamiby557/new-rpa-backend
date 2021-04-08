package com.szcinda.rpa.service;

import com.szcinda.rpa.job.DynamicJob;
import com.szcinda.rpa.repository.JobEntity;
import com.szcinda.rpa.repository.JobEntityRepository;
import org.quartz.*;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by EalenXie on 2018/6/4 14:25
 */
@Service
@Transactional
public class DynamicJobService {

    private final JobEntityRepository repository;

    public DynamicJobService(JobEntityRepository repository) {
        this.repository = repository;
    }

    //通过Id获取Job
    public JobEntity getJobEntityById(String id) {
        return repository.findById(id).orElseThrow(() -> new ExpressionException("根据[" + id + "]没有找到相应记录"));
    }

    //从数据库中加载获取到所有Job
    public List<JobEntity> loadJobs() {
        return repository.findAll();
    }

    //获取JobDataMap.(Job参数对象)
    public JobDataMap getJobDataMap(JobEntity job) {
        JobDataMap map = new JobDataMap();
        map.put("name", job.getName());
        /*map.put("jobGroup", job.getJobGroup());
        map.put("cronExpression", job.getCron());
        map.put("parameter", job.getParameter());
        map.put("jobDescription", job.getDescription());
        map.put("vmParam", job.getVmParam());
        map.put("jarPath", job.getJarPath());*/
        map.put("status", job.getStatus());
        return map;
    }

    //获取JobDetail,JobDetail是任务的定义,而Job是任务的执行逻辑,JobDetail里会引用一个Job Class来定义
    public JobDetail getJobDetail(JobKey jobKey, String description, JobDataMap map) {
        return JobBuilder.newJob(DynamicJob.class)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(map)
                .storeDurably()
                .build();
    }

    //获取Trigger (Job的触发器,执行规则)
    public Trigger getTrigger(JobEntity job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(job.getName(), job.getJobQueue())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
                .build();
    }

    //获取JobKey,包含Name和Group
    public JobKey getJobKey(JobEntity job) {
        return JobKey.jobKey(job.getName(), job.getJobQueue());
    }
}
