package cn.pyethel.remind.controller;

import cn.pyethel.remind.common.lang.Result;
import cn.pyethel.remind.utils.HttpRequestUtils;
import cn.pyethel.remind.entity.User;
import cn.pyethel.remind.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * date: 2020/12/4 21:59
 *
 * @author pyethel
 */
@RestController
@RequestMapping("/app")
@ConfigurationProperties(prefix = "vx")
public class AppController {
    private String appId;
    private String appSecret;
    private final UserService userService;

    public AppController(UserService userService) {
        this.userService = userService;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @GetMapping("/login")
    public Result doLogin(String code) throws JsonProcessingException {
        String urlPath = "https://api.weixin.qq.com/sns/jscode2session";
        String params =
                String.format("appid=%s", appId) +
                        String.format("&secret=%s", appSecret) +
                        String.format("&js_code=%s", code) +
                        String.format("&grant_type=%s", "authorization_code");
        String data = HttpRequestUtils.sendGet(urlPath,params);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(data);
        String openid = rootNode.path("openid").asText();
        User user = userService.getUserByOpenId(openid);
        if(user==null)
        {
            String token = UUID.randomUUID().toString();
            userService.saveUser(openid, token);
            return Result.succ(token);
        }else{
            return Result.succ(user.getToken());
        }
    }
}
