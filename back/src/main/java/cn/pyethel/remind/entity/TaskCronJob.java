package cn.pyethel.remind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * date: 2020/12/6 16:08
 *
 * @author pyethel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCronJob {
    private Long id;
    private String cron;
    private String jobName;
    private String jobClassName;
    private String jobDescription;
    private String jobNumber;
    private Boolean enabled;
    private String openid;
    private String jobTime;
}
