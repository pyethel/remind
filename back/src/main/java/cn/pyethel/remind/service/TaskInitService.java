package cn.pyethel.remind.service;

import cn.pyethel.remind.entity.TaskCronJob;
import cn.pyethel.remind.entity.User;
import cn.pyethel.remind.mapper.TaskCronJobMapper;
import cn.pyethel.remind.utils.TaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.quartz.CronExpression.isValidExpression;

/**
 * date: 2020/12/8 22:41
 *
 * @author pyethel
 */
@Service
@Slf4j
public class TaskInitService {

    private final Scheduler scheduler;
    private final UserService userService;
    private final TaskCronJobMapper cronJobMapper;

    public TaskInitService(
            Scheduler scheduler, UserService userService,
            TaskCronJobMapper cronJobMapper) {
        this.scheduler = scheduler;
        this.userService = userService;
        this.cronJobMapper = cronJobMapper;
    }

    public void deleteOneById(Long id){
        cronJobMapper.deleteOneById(id);
    }
    public TaskCronJob findOneById(Long id) {
        return cronJobMapper.queryOneById(id);
    }

    public List<TaskCronJob> findAll() {
        return cronJobMapper.queryAll();
    }

    public List<TaskCronJob> findAllEnabledByOpenid(String openid) {
        return cronJobMapper.queryAllEnabledByOpenid(openid);
    }

    public List<TaskCronJob> findAllByOpenid(String openid) {
        return cronJobMapper.queryAllByOpenid(openid);
    }

    public void updateJobNumberAndJobNameById(TaskCronJob job) {
        cronJobMapper.updateJobNumberAndJobNameById(job);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateEnabledById(Long id) {
        TaskCronJob job = cronJobMapper.queryOneById(id);
        job.setEnabled(!job.getEnabled());
        cronJobMapper.updateEnabledById(job);
    }

    private String time2Cron(String taskTime){
        String[] dateTime = taskTime.split("\\s+");
        String[] date = dateTime[0].split("-");
        String[] time = dateTime[1].split(":");
        return "0 " + time[1] + " " + time[0] + " " + date[2] + " " + date[1] + " ? " + date[0];
    }

    public void edit(Long id, String input, String taskTime){
        TaskCronJob job = this.findOneById(id);
        String cron = time2Cron(taskTime);
        job.setCron(cron);
        job.setJobDescription(input);
        job.setJobTime(taskTime);
        job.setEnabled(true);
        cronJobMapper.updateCronAndJobDescriptionAndJobTimeAndEnabledById(job);
        this.initOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(String taskTime, String input, String token) {
        try {
            TaskCronJob job = new TaskCronJob();
            //构建任务属性
            String cron = time2Cron(taskTime);
            User user = userService.getUserByToken(token);
            String openid = user.getOpenid();

            job.setCron(cron);
            job.setEnabled(true);
            job.setJobClassName("cn.pyethel.remind.quartz.PlanJob");
            job.setJobDescription(input);
            job.setOpenid(openid);
            job.setJobTime(taskTime);
            int res = cronJobMapper.save(job);
            long id = 0;
            if (1 == res) {
                id = job.getId();
                job.setJobNumber("CronJob_" + id);
                job.setJobName("CronJob" + id);
                // 持久化到数据库
                this.updateJobNumberAndJobNameById(job);
            }
            // 更新调度器
            this.initOne(id);
        } catch (Exception e) {
            log.error("定时任务保存失败...");
            log.error(e.getMessage());
        }

    }

    /**
     * 依赖注入完后立即执行
     * 初始化
     */
    @PostConstruct
    public void init() {
        if (scheduler == null) {
            log.error("初始化定时任务组件失败，Scheduler is null...");
            return;
        }

        // 初始化基于cron时间配置的任务列表
        try {
            initCronJobs(scheduler);
        } catch (Exception e) {
            log.error("init cron tasks error," + e.getMessage(), e);
        }

        try {
            log.info("The scheduler is starting...");
            scheduler.start(); // start the scheduler
        } catch (Exception e) {
            log.error("The scheduler start is error," + e.getMessage(), e);
        }
    }

    public void initOne(Long id) {
        if (scheduler == null) {
            log.error("保存定时任务组件失败，Scheduler is null...");
            return;
        }
        // 初始化基于cron时间配置的任务列表
        try {
            initCronJob(scheduler, id);
        } catch (Exception e) {
            log.error("init cron tasks error," + e.getMessage(), e);
        }
    }

    private void initCronJob(Scheduler scheduler, Long jobId) {
        TaskCronJob job = this.findOneById(jobId);
        if (job != null) {
            scheduleCronJob(job, scheduler);
        }
    }

    /**
     * 初始化所有任务（基于cron触发器）
     */
    private void initCronJobs(Scheduler scheduler) {
        Iterable<TaskCronJob> jobList = this.findAll();
        if (jobList != null) {
            for (TaskCronJob job : jobList) {
                scheduleCronJob(job, scheduler);
            }
        }
    }

    /**
     * 安排任务(基于cron触发器)
     *
     * @param job       job
     * @param scheduler scheduler
     */
    private void scheduleCronJob(TaskCronJob job, Scheduler scheduler) {
        if (job != null && StringUtils.isNotBlank(job.getJobName()) && StringUtils.isNotBlank(job.getJobClassName())
                && StringUtils.isNotBlank(job.getCron()) && scheduler != null) {
            if (!job.getEnabled()) {
                return;
            }

            try {
                JobKey jobKey = TaskUtils.genCronJobKey(job);

                if (!scheduler.checkExists(jobKey)) {
                    // This job doesn't exist, then add it to scheduler.
                    log.info("Add new cron job to scheduler, jobName = " + job.getJobName());
                    this.newJobAndNewCronTrigger(job, scheduler, jobKey);
                } else {
                    log.info("Update cron job to scheduler, jobName = " + job.getJobName());
                    this.updateCronTriggerOfJob(job, scheduler, jobKey);
                }
            } catch (Exception e) {
                log.error("ScheduleCronJob is error," + e.getMessage(), e);
            }
        } else {
            log.error("Method scheduleCronJob arguments are invalid.");
        }
    }


    /**
     * 新建job和trigger到scheduler(基于cron触发器)
     *
     * @param job       job
     * @param scheduler scheduler
     * @param jobKey    jobKey
     * @throws SchedulerException     e
     * @throws ClassNotFoundException e
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void newJobAndNewCronTrigger(TaskCronJob job, Scheduler scheduler, JobKey jobKey)
            throws SchedulerException, ClassNotFoundException {
        TriggerKey triggerKey = TaskUtils.genCronTriggerKey(job);

        String cronExpr = job.getCron();
        if (!isValidExpression(cronExpr)) {
            return;
        }

        // get a Class object by string class name of job;
        Class jobClass = Class.forName(job.getJobClassName().trim());
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .withDescription(job.getJobDescription())
                .build();
        jobDetail.getJobDataMap().put("openid", job.getOpenid());
        jobDetail.getJobDataMap().put("description", job.getJobDescription());
        jobDetail.getJobDataMap().put("time", job.getJobTime());
        jobDetail.getJobDataMap().put("id", job.getId());
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .forJob(jobKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr)
                        .withMisfireHandlingInstructionDoNothing())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 更新job的trigger(基于cron触发器)
     *
     * @param job       job
     * @param scheduler scheduler
     * @param jobKey    jobKey
     * @throws SchedulerException e
     */
    private void updateCronTriggerOfJob(TaskCronJob job, Scheduler scheduler, JobKey jobKey) throws SchedulerException {
        TriggerKey triggerKey = TaskUtils.genCronTriggerKey(job);
        String cronExpr = job.getCron().trim();

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

        for (int i = 0; triggers != null && i < triggers.size(); i++) {
            Trigger trigger = triggers.get(i);
            TriggerKey curTriggerKey = trigger.getKey();

            if (TaskUtils.isTriggerKeyEqual(triggerKey, curTriggerKey)) {
                if (!(trigger instanceof CronTrigger)
                        || !cronExpr.equalsIgnoreCase(((CronTrigger) trigger).getCronExpression())) {
                    if (isValidExpression(job.getCron())) {
                        // Cron expression is valid, build a new trigger and
                        // replace the old one.
                        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                                .withIdentity(triggerKey)
                                .forJob(jobKey)
                                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr)
                                        .withMisfireHandlingInstructionDoNothing())
                                .build();
                        scheduler.rescheduleJob(curTriggerKey, newTrigger);
                    }
                }  // Don't need to do anything.

            } else {
                // different trigger key ,The trigger key is illegal, unschedule
                // this trigger
                scheduler.unscheduleJob(curTriggerKey);
            }

        }

    }


}