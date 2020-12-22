package cn.pyethel.remind.quartz;

import cn.pyethel.remind.common.weixin.AccessTokenFactory;
import cn.pyethel.remind.service.TaskInitService;
import cn.pyethel.remind.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * date: 2020/12/10 19:06
 *
 * @author pyethel
 */
@Slf4j
@Component
public class PlanJob implements Job {

    @Autowired
    private TaskInitService taskInitService;

    final String templateId = "baL1uvWBmJ668_EYGKm7Q6JXiOw-7oAi0QvZnGgH2J8";

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        String openid = map.getString("openid");
        String description = map.getString("description");
        String time = map.getString("time");
        Long id = map.getLong("id");
        String accessToken = AccessTokenFactory.getToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
        //构建POST请求消息体参数
        Map<String, Object> param = new HashMap<>(16);
        param.put("touser", openid);
        param.put("template_id", templateId);
        param.put("miniprogram_state", "formal");
        param.put("page","pages/index/index");
        Map<String, Object> data = new HashMap<>(16);
        Map<String, Object> value1 = new HashMap<>(16);
        Map<String, Object> value2 = new HashMap<>(16);
        value1.put("value", description);
        value2.put("value", time);
        data.put("thing1", value1);
        data.put("time2", value2);
        param.put("data", data);
        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(param);
        log.info(s);
        //发送http请求到微信接口服务，获取订阅信息提醒
        Map post = HttpUtils.post(url, param);
        log.info(String.valueOf(post));
        //更新Enabled状态
        taskInitService.updateEnabledById(id);
    }

}
