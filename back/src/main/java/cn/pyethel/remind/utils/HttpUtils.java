package cn.pyethel.remind.utils;

import cn.gjing.http.HttpClient;
import cn.gjing.http.HttpMethod;

import java.util.Map;

/**
 * @author pyethel
 */
public class HttpUtils {

    public static String get(String url, Map<String, Object> param) {

        return HttpClient.builder(url, HttpMethod.GET, String.class)
                .param(param)
                .execute()
                .get();
    }

    public static String get(String url) {

        return HttpClient.builder(url, HttpMethod.GET, String.class)
                .execute()
                .get();
    }

    public static Map post(String url, Map<String, Object> param) {
        return HttpClient.builder(url, HttpMethod.POST, Map.class)
                .body(param)
                .execute()
                .get();
    }
}
