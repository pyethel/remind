package cn.pyethel.remind.controller;

import cn.pyethel.remind.common.lang.Result;
import cn.pyethel.remind.common.weixin.AccessTokenFactory;
import cn.pyethel.remind.entity.TaskCronJob;
import cn.pyethel.remind.entity.User;
import cn.pyethel.remind.entity.dto.JobDto;
import cn.pyethel.remind.entity.vo.JobVo;
import cn.pyethel.remind.service.TaskInitService;
import cn.pyethel.remind.service.UserService;
import cn.pyethel.remind.utils.Do2VoUtils;
import cn.pyethel.remind.utils.HttpRequestUtils;
import cn.pyethel.remind.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * date: 2020/12/6 18:39
 *
 * @author pyethel
 */
@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {
    private final TaskInitService taskInitService;
    private final UserService userService;

    public PlanController(
            TaskInitService taskInitService,
            UserService userService) {
        this.taskInitService = taskInitService;
        this.userService = userService;
    }


    @PostMapping("/create")
    public Result createCronJob(@RequestBody @Validated JobDto jobDto) throws JsonProcessingException {
        String taskTime = jobDto.getTime();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime ldt = LocalDateTime.parse(taskTime, df);
        String format = df.format(now);
        if (now.compareTo(ldt) >= 0) {
            return Result.fail("任务时间已过无效，请重新设置");
        }
        log.info("taskTime = {}, now = {} ", taskTime, format);
        String input = jobDto.getInput();
        String accessToken = AccessTokenFactory.getToken();
        String url = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=" + accessToken;
        Map<String, Object> params = new HashMap<>(1);
        params.put("content",input);
        Map res = HttpUtils.post(url, params);
        int errcode = (int)res.get("errcode");
        if (errcode == 87014) {
            return Result.fail("内容含有违法违规内容");
        }
        String token = jobDto.getToken();
        //持久化定时任务
        taskInitService.save(taskTime, input, token);
        return Result.succ(null);
    }

    @GetMapping("/list")
    public Result getJobList(String token) {
        User user = userService.getUserByToken(token);
        String openid = user.getOpenid();
        List<JobVo> jobVos = Do2VoUtils.copyList(taskInitService.findAllByOpenid(openid), JobVo.class);
        return Result.succ(jobVos);
    }

    @GetMapping("/delete")
    public Result deleteCronJob(Long id) {
        taskInitService.deleteOneById(id);
        return Result.succ(null);
    }

    @GetMapping("/detail/{id}")
    public Result jobDetailById(@PathVariable Long id) {
        TaskCronJob job = taskInitService.findOneById(id);
        JobVo jobVo = Do2VoUtils.copyObj(job, JobVo.class);
        return Result.succ(jobVo);
    }

    @GetMapping("/edit/{id}")
    public Result editJobById(@PathVariable Long id, String input, String time) {
        taskInitService.edit(id, input, time);

        return Result.succ(null);
    }

}
