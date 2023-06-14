package com.huawei.sdc.restful.controller;

import com.alibaba.fastjson.JSONObject;
import com.huawei.sdc.restful.activeregister.ActiveRegisterServerDemo;
import com.huawei.sdc.restful.activeregister.ActiveRegisterSyncWorker;
import com.huawei.sdc.restful.bean.AlarmBean;
import com.huawei.sdc.restful.bean.DeviceBean;
import com.huawei.sdc.restful.bean.SDCDeviceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("huawei")
public class HuaWeiController {
/**
 * { "DeviceName": "HoloSens SDC", "Manufacturer": "huawei", "DeviceType": "M2141-10-EI", "SerialNumber": "2102412733WMMA000132",
 * "DeviceVersion": { "Software": "SDC 9.0.0.SPC303", "Uboot": "U-Boot 2020.01", "Kernel": "Linux 4.19.90", "Hardware": "A" } }
 * { "param": [ { "support": false, "channelNo": 101, "UUID": "a0a9efa3-d1fc-3535-2a19-b16265531367" } ] }
 * 2102412733WMMA000132
 */
    // 解决华为报警信息类型错误的问题 Content-Type: application/
    @RequestMapping(value = "test1", method = RequestMethod.POST)
    public Object test1(DeviceBean deviceBean, HttpServletRequest request) throws IOException {
        // 从请求上下文里获取Request对象
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        IOUtils.copy(request.getInputStream(), baos);
        ServletInputStream inputStream = request.getInputStream();
        AlarmBean alarmBean = JSONObject.parseObject(inputStream, AlarmBean.class);
        log.info("接收到告警参数--deviceBean " + deviceBean);
        log.info("接收到告警参数" + alarmBean);
        return "成功";
    }
    @PostMapping("test2")
    public Object test2( DeviceBean deviceBean, HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        AlarmBean alarmBean = JSONObject.parseObject(inputStream, AlarmBean.class);
//        String requestBody = getRequestBody(request);
//        AlarmBean alarmBean = JSONObject.parseObject(requestBody, AlarmBean.class);
        log.info("接收到告警参数--deviceBean " + deviceBean);
//        log.info("接收到告警参数" + requestBody);
        log.info("接收到告警参数" + alarmBean);
        return "成功";
    }
    public String getRequestBody(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // 处理异常
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 处理异常
                }
            }
        }
        String requestBody = sb.toString();
        // 处理请求体数据
        return requestBody;
    }
    @GetMapping("test")
    public Object test(String deviceNum) throws IOException, NoSuchAlgorithmException {
        ActiveRegisterSyncWorker worker = ActiveRegisterServerDemo.getDeviceWorker(deviceNum);
        JSONObject systemInfo = worker.getSystemInfo();
        return systemInfo;
    }
    // 云台查询基础能力
    @GetMapping("capability")
    public Object capability(String deviceNum) throws IOException, NoSuchAlgorithmException {
        ActiveRegisterSyncWorker worker = ActiveRegisterServerDemo.getDeviceWorker(deviceNum);
        JSONObject capability = worker.getCapability();
        return capability;
    }
    @GetMapping("topics")
    public Object topics(String deviceNum) throws IOException, NoSuchAlgorithmException {
        ActiveRegisterSyncWorker worker = ActiveRegisterServerDemo.getDeviceWorker(deviceNum);
        JSONObject jsonObject = worker.setSubscriptionTopics();
        return jsonObject;
    }
    @GetMapping("topicsDelete")
    public Object topicsDelete(String deviceNum,Integer id) throws IOException, NoSuchAlgorithmException {
        ActiveRegisterSyncWorker worker = ActiveRegisterServerDemo.getDeviceWorker(deviceNum);
        JSONObject jsonObject = worker.setSubscriptionTopicsDelete(id);
        return jsonObject;
    }
    @GetMapping("topicsGet")
    public Object topicsGet(String deviceNum,Integer id) throws IOException, NoSuchAlgorithmException {
        ActiveRegisterSyncWorker worker = ActiveRegisterServerDemo.getDeviceWorker(deviceNum);
        JSONObject jsonObject = worker.getSubscriptionTopics(id);
        return jsonObject;
    }
    @GetMapping("osdSet")
    public Object osdSet(String deviceNum) throws IOException, NoSuchAlgorithmException {
        ActiveRegisterSyncWorker worker = ActiveRegisterServerDemo.getDeviceWorker(deviceNum);
        JSONObject jsonObject = worker.setOsd();
        return jsonObject;
    }
}
