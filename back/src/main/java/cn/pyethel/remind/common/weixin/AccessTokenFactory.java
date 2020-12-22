package cn.pyethel.remind.common.weixin;

import cn.pyethel.remind.utils.HttpRequestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * date: 2020/12/21 19:35
 *
 * @author pyethel
 */
public class AccessTokenFactory {

    private final static String APP_ID = "xxx";
    private final static String APP_SECRET = "xxx";
    private static volatile AccessToken accessToken = new AccessToken();
    private final static String URL = "https://api.weixin.qq.com/cgi-bin/token";
    private final static String PARAMS = "grant_type=client_credential&appid=" + APP_ID + "&secret=" + APP_SECRET;

    /**
     * @return 获取后台接口调用凭证access_token
     * @throws JsonProcessingException e
     */
    public static String getToken() throws JsonProcessingException {
        if (accessToken.getToken() == null || accessToken.getExpires().compareTo(LocalDateTime.now()) <= 0) {
            synchronized (AccessToken.class) {
                if (accessToken.getToken() == null || accessToken.getExpires().compareTo(LocalDateTime.now()) <= 0) {
                    String result = HttpRequestUtils.sendGet(URL, PARAMS);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(result);
                    int expiresIn = rootNode.path("expires_in").asInt();
                    String token = rootNode.path("access_token").asText();
                    LocalDateTime expires = LocalDateTime.now().plusSeconds(expiresIn);
                    accessToken.setExpires(expires);
                    accessToken.setToken(token);
                    return token;
                }
            }
        }
        return accessToken.getToken();
    }
}

@Data
class AccessToken {
    private String token;
    private LocalDateTime expires;
}
