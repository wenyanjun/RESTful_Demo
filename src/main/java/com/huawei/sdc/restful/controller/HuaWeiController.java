package com.huawei.sdc.restful.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huawei.sdc.restful.activeregister.ActiveRegisterServerDemo;
import com.huawei.sdc.restful.activeregister.ActiveRegisterSyncWorker;
import com.huawei.sdc.restful.bean.AlarmBean;
import com.huawei.sdc.restful.bean.DeviceBean;
import com.huawei.sdc.restful.bean.SDCDeviceInfo;
import com.huawei.sdc.restful.utils.ImageUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
@RequestMapping("huawei")
@SuppressWarnings("all")
public class HuaWeiController {
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        String str = "/home/warn/34020000001320000305-322/告警图片/20230831201412483_A01_000000D4.jpg";
        if (!str.contains(".jpg")) return;

        String[] split = str.split("/");
        System.out.println(Arrays.toString(split));
        if (split.length >= 3){
            String s = split[3];
            String[] split1 = s.split("-");
            System.out.println(Arrays.toString(split1));
        }
    }
    @RequestMapping(value = "test13", method = RequestMethod.POST)
    public Object test(@RequestBody AlarmBean alarmBean, HttpServletRequest request) throws IOException {

        executorService.submit(new Runnable() {
            @Override
            public void run() {

            }
        });

        log.info("alarmBean--" + alarmBean);
        return "成功";
    }
/**    application/
 *
 * { "DeviceName": "HoloSens SDC", "Manufacturer": "huawei", "DeviceType": "M2141-10-EI", "SerialNumber": "2102412733WMMA000132",
 * "DeviceVersion": { "Software": "SDC 9.0.0.SPC303", "Uboot": "U-Boot 2020.01", "Kernel": "Linux 4.19.90", "Hardware": "A" } }
 * { "param": [ { "support": false, "channelNo": 101, "UUID": "a0a9efa3-d1fc-3535-2a19-b16265531367" } ] }
 * 2102412733WMMA000132
 * 接收到告警参数AlarmBean(alarmSource=Yolov3, alarmName=uuid, commonAlarmType=1,
 * obj=[{"globalObjId":1,"closeUp":[{"picAttr":{"picwigh":"small","piclen":"large"},"closeUpPic":""}],
 * "objId":1,"objAttr":{"glass":"yes","hair":"yes"},"describeInfo":"ren","objPosR":{"x":3161,"width":3970,"y":2389,"height":4485}}],
 * picSnapshotTimes=18446744072350230992, producerName=app_agent, picSnapshotDstOffset=1, snapshotTzone=8, algType=dog_detect,
 * PTS=18446744072350230992,
 * metaTypeMask=16, metaName=dog_detect, panorama=[{"picAttr":{"picwigh":"small","piclen":"large"},"panoramaPic":""}])
 */
    // 解决华为报警信息类型错误的问题 Content-Type: application/
    @RequestMapping(value = "test1", method = RequestMethod.POST)
    public Object test1(@RequestBody AlarmBean alarmBean, DeviceBean deviceBean, HttpServletRequest request) throws IOException {
        log.info("alarmBean--" + alarmBean);
        // 从请求上下文里获取Request对象
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        IOUtils.copy(request.getInputStream(), baos);
//        ServletInputStream inputStream = request.getInputStream();
//        Object o = JSONObject.parseObject(baos.toByteArray(), Map.class);
//        log.info("接收到告警参数--o" + o);
//        AlarmBean alarmBean = JSONObject.parseObject(baos.toByteArray(), AlarmBean.class);
        JSONArray obj = alarmBean.getObj();
        Map<String,Object> o = (Map<String, Object>) obj.get(0);
        // 取出坐标 图片坐标
        Map<String,Integer> objPosR = (Map<String, Integer>) o.get("objPosR");
        // 拿到大图
        JSONArray panorama = alarmBean.getPanorama();
        Map<String,Object> o2 = (Map<String, Object>) panorama.get(0);
        String panoramaPic = o2.get("panoramaPic").toString();

        Class<?> closeUp1 = o.get("closeUp").getClass();
        log.info("o----" + closeUp1);
        ArrayList closeUp = (ArrayList) o.get("closeUp");
        Map o1 = (Map) closeUp.get(0);
        String closeUpPic = o1.get("closeUpPic").toString();
        long timeMillis = System.currentTimeMillis();
        // 图片路径
        String path ="D:\\桌面1\\管网\\华为摄像机\\pic\\" +  deviceBean.getDeviceName()  + "-closeUpPic-" + timeMillis + ".jpg";
        String path1 ="D:\\桌面1\\管网\\华为摄像机\\pic\\" +  deviceBean.getDeviceName()  + "-panoramaPic-" + timeMillis + ".jpg";
        ImageUtils.base64ToImage(closeUpPic,path);
        ImageUtils.base64ToImage(panoramaPic,path1);

        log.info("图片坐标--objPosR" + objPosR);

        log.info("接收到告警参数--deviceBean " + deviceBean);
        log.info("接收到告警参数" + alarmBean);
        return "成功";
    }
    @PostMapping("test2")
    public Object test2(DeviceBean deviceBean, HttpServletRequest request) throws IOException {
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
