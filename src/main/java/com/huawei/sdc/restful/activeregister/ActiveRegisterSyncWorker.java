package com.huawei.sdc.restful.activeregister;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpParser;
import com.huawei.sdc.restful.http.HttpHeadersHelper;
import com.huawei.sdc.restful.http.HttpReqLineHelper;
import com.huawei.sdc.restful.bean.SDCDeviceInfo;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActiveRegisterSyncWorker {

    /* 主动注册长连接对端设备端口 */
    private static String remoteHost;

    /* 主动注册长连接对端设备端口 */
    private static int remotePort;

    /* 主动注册长连接输出流，基于此对象接收主动注册请求及后续请求的响应 */
    private static InputStream inputStream;

    /* 主动注册长连接输出流，基于此对象发送后续请求 */
    private static OutputStream outputStream;

    /* 当前使用的鉴权算法，必须与设备端配置一致 */
    private final static String authMethod = "MD5";

    /* 设备端北向接入账号、密码 */
    private static String userName = "ApiAdmin";
    private static String password = "admin888";

    /* 设备信息 */
    private static SDCDeviceInfo deviceInfo = new SDCDeviceInfo();
    public SDCDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public ActiveRegisterSyncWorker(Socket acceptSocket) {
        try {
            InetSocketAddress remoteAdd = (InetSocketAddress)acceptSocket.getRemoteSocketAddress();
            remotePort = remoteAdd.getPort();
            remoteHost = remoteAdd.getHostName();
            inputStream = acceptSocket.getInputStream();
            outputStream = acceptSocket.getOutputStream();
            System.out.println("remote host: " + remoteHost + ", port: " + remotePort);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void dealWithActiveRegisterReq (String natUrl) throws IOException {
        reqLineObj = null;
        httpHeadersHelper = null;
        httpBody = null;
        String s = parseHttpMessage();

        /* 设备端配置的主动注册URL应与服务端一致 */
//        if (!reqLineObj.url.equals(natUrl)) {
//            System.out.println("wrong active register request!! reqUrl: " + reqLineObj.url + ", natUrl: " + natUrl);
//        }
        try {
            // { "DeviceName": "HoloSens SDC", "Manufacturer": "huawei", "DeviceType": "M2141-10-EI", "SerialNumber": "2102412733WMMA000132", "DeviceVersion": { "Software": "SDC 9.0.0.SPC303", "Uboot": "U-Boot 2020.01", "Kernel": "Linux 4.19.90", "Hardware": "A" } }
            deviceInfo = JSONObject.parseObject(s, SDCDeviceInfo.class);
            if (deviceInfo == null) return;
            log.info("deviceInfo--" + deviceInfo);

            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Connection: keep-alive\r\n\r\n";
            sendRequest(httpResponse);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /*
    * 示例，获取设备基础信息：GET /SDCAPI/V1.0/MiscIaas/System
    * TODO: 可参考此方法实现其他接口调用。
    * */
    public JSONObject getSystemInfo() throws IOException, NoSuchAlgorithmException {
        String method = "GET";
        String url = "/SDCAPI/V1.0/MiscIaas/System";
        System.out.println("发送请求:" + url);
        JSONObject jsonObject = sendReqAndRecvBody(method, url, null);
        return jsonObject;
    }

    // 云台查询
    public JSONObject getCapability() throws IOException, NoSuchAlgorithmException {
        String method = "GET";
        String url = "/SDCAPI/V1.0/PtzIaas/Capability";
        System.out.println("发送请求:" + url);
        JSONObject jsonObject = sendReqAndRecvBody(method, url, null);
        return jsonObject;
    }
    // 预置位设置
    public void getPresetPositions() throws IOException, NoSuchAlgorithmException {
        String method = "GET";
        String url = "/SDCAPI/V1.0/Ptz/PresetPositions?UUID={通道UUID}";
        System.out.println("发送请求:" + url);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("index", 1);
        map.put("name", "123");
        map.put("enableFocusInfo", 0);
        String s = JSONObject.toJSONString(map);
        log.info("发送的数据--" + s);
        sendReqAndRecvBody(method,  url, s.getBytes());
    }
    // 告警订阅
    public JSONObject setSubscriptionTopics() throws IOException, NoSuchAlgorithmException {
        String method = "POST";
        String url = "/SDCAPI/V1.0/Notification/Subscription/Topics";

        System.out.println("发送请求:" + url);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("topic", "alarm.paas.sdc");
        map.put("url", "http://172.16.1.58:8081/huawei/test1");
        ArrayList<Map> maps = new ArrayList<Map>();
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        maps.add(map1);
        map.put("filter", maps);
//        map1.put("uuid","a0a9efa3-d1fc-3535-2a19-b16265531367");
        map1.put("type","event");
        map1.put("app","Yolov3");
        map1.put("name","face");
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map.put("acceptType",map2);
        map2.put("dataType","json");
        String s = JSONObject.toJSONString(map);
        log.info("发送的数据--" + s);
        JSONObject jsonObject = sendReqAndRecvBody(method, url, s.getBytes());
        return jsonObject;
    }
    // 告警订阅删除
    public JSONObject setSubscriptionTopicsDelete(Integer id) throws IOException, NoSuchAlgorithmException {
        String method = "DELETE";
        String url = "/SDCAPI/V1.0/Notification/Subscription/Topics?ID=" + id;
        JSONObject jsonObject = sendReqAndRecvBody(method, url, null);
        return jsonObject;
    }
    // 告警订阅获取
    // GET /SDCAPI/V1.0/Notification/Subscription/Topics
    public JSONObject getSubscriptionTopics(Integer id) throws IOException, NoSuchAlgorithmException {
        String method = "GET";
        String url = "/SDCAPI/V1.0/Notification/Subscription/Topics";
        JSONObject jsonObject = sendReqAndRecvBody(method, url, null);
        return jsonObject;
    }
    // 获取osd模板
    // GET /SDCAPI/V1.0/OsdIaas/Channels/Osd?UUID={通道号}（或ChannelId={通道
    //ID}）&TemplateId={模板ID}
    public JSONObject getOsdIaas() throws IOException, NoSuchAlgorithmException {
        String method = "GET";
        String url = "/SDCAPI/V1.0/OsdIaas/Channels/Osd?ChannelId=101";
        JSONObject jsonObject = sendReqAndRecvBody(method, url, null);
        return jsonObject;
    }
    // 设置osd id
    public JSONObject setOsdIaas(Object obj) throws IOException, NoSuchAlgorithmException {
        String method = "PUT";
        String url = "/SDCAPI/V1.0/OsdIaas/Channels/Osd?ChannelId=101";
        String s = JSONObject.toJSONString(obj);
        JSONObject jsonObject = sendReqAndRecvBody(method, url, s.getBytes());
        return jsonObject;
    }
    // 设置osd
    public JSONObject setOsd() throws IOException, NoSuchAlgorithmException {
        JSONObject osdIaas = this.getOsdIaas();
        osdIaas.remove("numOfTemplate");
        JSONArray templateArry = osdIaas.getJSONArray("osdTemplateArry");
        JSONObject template = (JSONObject) templateArry.get(0);
        template.put("enable", 1);
        template.put("numOfOsdParam", 4);
//        JSONArray osdParam = template.getJSONArray("osdParam");
        ArrayList<Map<String,Object>> osdParam = new ArrayList<>();
        template.put("osdParam", osdParam);
        HashMap<String, Object> map = new HashMap<>();
        map.put("osdId", 0);
        map.put("osdEnable", 1);
        HashMap<String, Object> osdRegion = new HashMap<>();
        osdRegion.put("originPos",0);
        osdRegion.put("startX",0);
        osdRegion.put("startY",4);
        osdRegion.put("width",384);
        osdRegion.put("height",64);
        map.put("osdRegion", osdRegion);
        map.put("formatLen", 103);
        map.put("contentLen", 25);
        map.put("osdFormat", "fontInverted=1;autoWrap=0;fontSize=64;fontColor=0;bgColor=16777215;fontTrans=0;bgTrans=100;lineSpace=0");
        map.put("osdContent", "${time@sys: YYYY-MM-DD}");
        osdParam.add(map);
        // 经纬度
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("osdId", 1);
        map1.put("osdEnable", 1);
        HashMap<String, Object> osdRegion1 = new HashMap<>();
        osdRegion1.put("originPos",0);
        osdRegion1.put("startX",731);
        osdRegion1.put("startY",0);
        osdRegion1.put("width",0);
        osdRegion1.put("height",0);
        map1.put("osdRegion", osdRegion1);
        map1.put("formatLen", 101);
        map1.put("contentLen", 29);
        map1.put("osdFormat", "fontInverted=1;autoWrap=0;fontSize=60;fontColor=16777215;bgColor=0;fontTrans=0;bgTrans=0;lineSpace=0");
        map1.put("osdContent", "经度113.3333,纬度42.3333");
        osdParam.add(map1);
        // 电量电压
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("osdId", 2);
        map2.put("osdEnable", 1);
        HashMap<String, Object> osdRegion2 = new HashMap<>();
        osdRegion2.put("originPos",0);
        osdRegion2.put("startX",1260);
        osdRegion2.put("startY",1020);
        osdRegion2.put("width",0);
        osdRegion2.put("height",0);
        map2.put("osdRegion", osdRegion2);
        map2.put("formatLen", 101);
        map2.put("contentLen", 45);
        map2.put("osdFormat", "fontInverted=1;autoWrap=0;fontSize=60;fontColor=16777215;bgColor=0;fontTrans=0;bgTrans=0;lineSpace=0");
        int bat = (int)(Math.random() * 100);
        int temp = (int)(Math.random() * 100);
        map2.put("osdContent", "电量"+ bat +"% 电压24v 温度"+ temp +"°C 充电电流12v");
        osdParam.add(map2);
        // 杆塔
        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("osdId", 3);
        map3.put("osdEnable", 1);
        HashMap<String, Object> osdRegion3 = new HashMap<>();
        osdRegion3.put("originPos",2);
        osdRegion3.put("startX",1467);
        osdRegion3.put("startY",8);
        osdRegion3.put("width",8);
        osdRegion3.put("height",0);
        map3.put("osdRegion", osdRegion3);
        map3.put("formatLen", 101);
        map3.put("contentLen", 13);
        map3.put("osdFormat", "fontInverted=1;autoWrap=0;fontSize=60;fontColor=16777215;bgColor=0;fontTrans=0;bgTrans=0;lineSpace=0");
        map3.put("osdContent", "SZHE043-1枪");
        osdParam.add(map3);
        log.info("发送数据---" + osdIaas);
        JSONObject jsonObject = this.setOsdIaas(osdIaas);
        return jsonObject;
    }
    // 发送数据
    private JSONObject sendReqAndRecvBody(String method, String url, byte[] body) throws IOException, NoSuchAlgorithmException {
        //String httpMessage = reqLine.toString() + headers.toString();
        String reqLineStr = method + " " + url + " HTTP/1.1\r\n";
        HttpReqLineHelper reqLine = new HttpReqLineHelper(reqLineStr);
        HttpHeadersHelper headers = new HttpHeadersHelper();
        headers.addHeader("Accept", "*/*");
        headers.addHeader("Host", remoteHost + ":" + remotePort);
        // keep-alive
        headers.addHeader("Connection", "keep-alive");
        headers.addHeader("Content-Type", "application/json");
        if (null != body){
            // 请求体带参数的要添加长度
            headers.addHeader("Content-Length", String.valueOf(body.length));
        }
        StringBuilder httpMessage = new StringBuilder();
        httpMessage.append(reqLine.toString());
        httpMessage.append(headers.toString());
        sendRequest(httpMessage.toString(), body);
//        if (null != body){
//            outputStream.write(body);
//        }
        // receive response
        String s = parseHttpMessage();
        if (null != reqLineObj && reqLineObj.statusCode == 401) {

            // deal with authentication
            headers.addHeader(httpHeadersHelper.getAuthorizationHeader(authMethod, userName, password, reqLine.method, reqLine.url));

            httpMessage = new StringBuilder();
            httpMessage.append(reqLine.toString()).append(headers.toString());
            log.info("授权失败----" + httpMessage.toString());

            sendRequest(httpMessage.toString(),body);
            s = parseHttpMessage();
        }
        return JSONObject.parseObject(s);
    }

    // 本次接收到的响应的消息
    /* HTTP消息请求行 */
    private static HttpReqLineHelper reqLineObj = null;
    /* HTTP消息头 */
    private static HttpHeadersHelper httpHeadersHelper = null;
    /* HTTP消息体 */
    private static byte[] httpBody = null;
    private String parseHttpMessage() throws IOException {
        reqLineObj = new HttpReqLineHelper(inputStream);

        httpHeadersHelper = new HttpHeadersHelper(HttpParser.parseHeaders(inputStream, "UTF-8"));
        int contentLen = httpHeadersHelper.contentLen;
        log.info("contentLen--" + contentLen);
        // read body
        httpBody = null;
        if (contentLen > 0) {
            httpBody = new byte[contentLen];
            int offset = 0;
            do{
                int readLen = inputStream.read(httpBody, offset, contentLen-offset);
                if (readLen < 0) {
                    // no more data
                    break;
                }
                offset += readLen;
            } while (offset < contentLen);
            // TODO: deal with message body， stored in httpBody. choose json library to decode it or save to file.

        }
        log.info("这里调用了---parseHttpMessage");
        String s = printHTTPMessage();
        return s;
    }

    /* 发送带二进制body的请求（涉及文件上传） */
    public void sendRequest(String reqLineAndHeaders, byte[] body) throws IOException {
        System.out.println("sendRequest: \n" + reqLineAndHeaders);
        outputStream.write(reqLineAndHeaders.getBytes());
        byte[] bytes = reqLineAndHeaders.getBytes();
        if (null != body) {
            outputStream.write(body);
//            bytes = byteMerger(bytes, body);
        }
//        outputStream.write(bytes);
    }
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
    /* 发送不带二进制body的请求 */
    public void sendRequest(String httpRequest) throws IOException {
        System.out.println(httpRequest);
        outputStream.write(httpRequest.getBytes());
    }

    private String printHTTPMessage() {
        if (null != reqLineObj) {
            log.info(reqLineObj.toString());
        }
        if (null != httpHeadersHelper) {
            log.info(httpHeadersHelper.toString());
        }
        String s = "";
        if (null != httpBody) {
            s = new String(httpBody);
            log.info("---body---" +  new String(httpBody));
        }
        return s;
    }
}
