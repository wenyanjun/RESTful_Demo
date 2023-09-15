package com.huawei.sdc.restful.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author coderyj
 * @date 2017/12/15.
 * @email 997745354@qq.com
 * ajax 回执
 */
@Data
public class JsonUtil {
    private static final Integer CSPID_FAIL_CODE = 400;
    private static final Integer CSPID_SUCCESS_CODE = 200;
    private static final Integer CSPID_NONE_DATA_CODE = 10001;
    //默认成功
    private boolean flag = true;
    private String msg;
    private Integer code;
    private Object data;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public JsonUtil() {
    }

    public JsonUtil(boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public JsonUtil(boolean flag, String msg, Integer code, Object data) {
        this.flag = flag;
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public JsonUtil(boolean flag, String msg, Integer code) {
        this.flag = flag;
        this.msg = msg;
        this.code = code;
    }
    public static JsonUtil error(String msg, Integer code, Object data) {
        return new JsonUtil(false, msg, code, data);
    }
    /**
     * restful 返回
     */
    public static JsonUtil error(String msg, Integer code) {
        return new JsonUtil(false, msg, code);
    }
    public static JsonUtil error(String msg, Object data) {
        return new JsonUtil(false, msg, CSPID_FAIL_CODE, data);
    }
    public static JsonUtil error(String msg) {
        return new JsonUtil(false, msg, CSPID_FAIL_CODE);
    }
    
    public static JsonUtil success(String msg) {
        return new JsonUtil(true, msg, CSPID_SUCCESS_CODE);
    }
    
    public static JsonUtil success(String msg, Integer code) {
        return new JsonUtil(true, msg, code);
    }
    public static JsonUtil success(String msg, Object data) {
        JsonUtil util = new JsonUtil(true, msg);
        util.setData(data);
        if (data == null){
            util.setCode(CSPID_NONE_DATA_CODE);
        }else {
            util.setCode(200);
        }
        return util;
    }
    public static JsonUtil success(String msg, Integer code, Object data) {
        JsonUtil util = new JsonUtil(true, msg);
        util.setData(data);
        if (data == null){
            util.setCode(CSPID_NONE_DATA_CODE);
        }else {
            util.setCode(code);
        }
        return util;
    }
    /**
     * java对象转json字符串
     */
    public static String object2Json(Object object){
        Object o = JSONObject.toJSON(object);
        if (o == null){
            return null;
        }
        return o.toString();
    }
    /**
     * java对象转json字符串
     */
    public static String stringify(Object object){
        Object o = JSONObject.toJSON(object);
        if (o == null){
            return null;
        }
        return o.toString();
    }

    /**
     * json 转java对象
     */
    public static Object json2Object(String json){
        try {
            return JSONObject.parseObject(json);
        }catch (Exception e){
            return JSONArray.parseArray(json);
        }
    }
    /**
     * json 转java对象
     */
    public static Object parse(String json){
        try {
            return JSONObject.parseObject(json);
        }catch (Exception e){
            return JSONArray.parseArray(json);
        }
    }
    /**
     * 从JSONObject中根据key获取value
     */
    public static Object getObj(JSONObject object, String key){
        Object o = null;
        if (object.containsKey("ids")){
            o = object.get("ids");
        }
        return o;
    }

    /**
     * 添加一个属性
     */
    public static JSONObject addOption(Object object, String key, Object value){
        JSONObject o = (JSONObject) JSONObject.toJSON(object);
        o.put(key, value);
        return o;
    }

    /**
     * 移除属性
     */
    public static Object removeOption(Object object, String key){
        JSONObject o = (JSONObject) JSONObject.toJSON(object);
        if (o.containsKey(key)){
            return o.remove(key);
        }
        return null;
    }
}
