package cn.pyethel.remind.utils;

import cn.pyethel.remind.entity.TaskCronJob;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * date: 2020/12/7 20:53
 *
 * @author pyethel
 * <p>
 * 负责为定时任务生产JobKey和TriggerKey。
 */
public class TaskUtils {
    private static final String CRON_JOB_GROUP_NAME = "cron_task_group";

    /**
     * 产生JobKey
     *
     * @param job job
     * @return jobKey
     */
    public static JobKey genCronJobKey(TaskCronJob job) {
        return new JobKey(job.getJobName().trim(), CRON_JOB_GROUP_NAME);
    }

    /**
     * 产生TriggerKey
     *
     * @param job job
     * @return triggerKey
     */
    public static TriggerKey genCronTriggerKey(TaskCronJob job) {
        return new TriggerKey("trigger_" + job.getJobName().trim(), CRON_JOB_GROUP_NAME);
    }

    /**
     * @param tk1 triggerKey1
     * @param tk2 triggerKey2
     * @return isEqual
     */
    public static boolean isTriggerKeyEqual(TriggerKey tk1, TriggerKey tk2) {
        return tk1.getName().equals(tk2.getName()) && ((tk1.getGroup() == null && tk2.getGroup() == null)
                || (tk1.getGroup() != null && tk1.getGroup().equals(tk2.getGroup())));
    }


}
