package cn.pyethel.remind.mapper;

import cn.pyethel.remind.entity.TaskCronJob;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * date: 2020/12/6 18:59
 *
 * @author pyethel
 */
@Mapper
@Repository
public interface TaskCronJobMapper {


    /**
     * 删除任务通过id查询
     * @param id id
     *
     */
    @Delete("delete from task_cron_job where id=#{id}")
    void deleteOneById(Long id);
    /**
     * 返回所有任务
     *
     * @return 任务列表
     */
    @Select("select * from task_cron_job")
    List<TaskCronJob> queryAll();

    /**
     * 通过id查询一个job
     *
     * @param id id
     * @return job
     */
    @Select("select * from task_cron_job where id=#{id}")
    TaskCronJob queryOneById(Long id);

    /**
     * 通过用户唯一标识查询出所有Enabled任务
     *
     * @param openid 用户唯一标识
     * @return 任务列表
     */
    @Select("select * from task_cron_job where openid=#{openid} and enabled=1")
    List<TaskCronJob> queryAllEnabledByOpenid(String openid);

    /**
     * 通过用户唯一标识查询出所有任务
     *
     * @param openid 用户唯一标识
     * @return 任务列表
     */
    @Select("select * from task_cron_job where openid=#{openid}")
    List<TaskCronJob> queryAllByOpenid(String openid);
    /**
     * 保存任务
     *
     * @param job 任务
     * @return 影响行数
     */
    @Insert("insert into task_cron_job" +
            "(cron, job_class_name, job_description, enabled, openid, job_time) " +
            "values " +
            "(#{cron}, #{jobClassName}, #{jobDescription}, #{enabled}, #{openid}, #{jobTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(TaskCronJob job);

    /**
     * 更新job cron and description
     * @param job 任务
     */
    @Update("update task_cron_job set cron=#{cron}, job_description=#{jobDescription}, job_time=#{jobTime}, enabled=#{enabled} where id=#{id}")
    void updateCronAndJobDescriptionAndJobTimeAndEnabledById(TaskCronJob job);
    /**
     * 更新任务 插入jobName,jobNumber
     * @param job job
     *
     */
    @Update("update task_cron_job set job_name=#{jobName}, job_number=#{jobNumber} where id=#{id}")
    void updateJobNumberAndJobNameById(TaskCronJob job);

    /**
     * 更新状态，执行后enabled = false
     *
     * @param job job
     *
     */
    @Update("update task_cron_job set enabled=#{enabled} where id=#{id}")
    void updateEnabledById(TaskCronJob job);
}
