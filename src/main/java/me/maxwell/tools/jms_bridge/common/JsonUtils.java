package me.maxwell.tools.jms_bridge.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxwell.Lee
 * @date 2018-10-19 18:04
 * @since   1.0.0
 */
public class JsonUtils {

    public static String toJsonStr(Map<String, Object> object) {
        return JSONObject.toJSONString(object, SerializerFeature.PrettyFormat);
    }

    public static String toJsonStr(Object object) {
        return JSONObject.toJSONString(object, SerializerFeature.PrettyFormat);
    }

    public static <T> T parseFromStr(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    public static <T> T parseFromStr(String jsonStr, TypeReference<T> type) {
        return JSON.parseObject(jsonStr, type);
    }

    public static <T> List<T> parseListFromStr(String jsonStr, Class<T> clazz) {
        return JSON.parseArray(jsonStr, clazz);
    }

    public static Map<String, Object> parseAsMapOfObj(String jsonStr) {
        JSONObject obj = JSON.parseObject(jsonStr);
        if (obj == null) return null;

        Map<String, Object> rst = new HashMap<>();
        for (String key : obj.keySet()) {
            rst.put(key, obj.get(key));
        }

        return rst;
    }

    public static Map<String, String> parseAsMapOfStr(String jsonStr) {
        JSONObject obj = JSON.parseObject(jsonStr);

        Map<String, String> rst = new HashMap<>();

        if (obj == null) return rst;

        for (String key : obj.keySet()) {
            Object val = obj.get(key);

            if (val == null) {
                rst.put(key, null);
            } else {
                rst.put(key, val.toString());
            }
        }

        return rst;
    }
}
