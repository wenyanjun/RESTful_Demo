package com.huawei.sdc.restful.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// image工具类
public class ImageUtils {
    /**
     * base64字符串转化成图片
     * @param imgStr
     * @return
     */
    public static boolean base64ToImage(String imgStr, String path) {
        //对字节数组字符串进行Base64解码并生成图片
        //图像数据为空
        if (imgStr == null) {
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            //Base64解码
            byte[] b = decoder.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                //调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            //新生成的图片
            OutputStream out = Files.newOutputStream(Paths.get(path));
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // 文件转base64
    public static String base64Encoding(String filePath){
        try {
            // base64图片前缀
//            String pre = "data:image/jpg;base64,";
            File file = new File(filePath);
            if (!file.exists()){
                return null;
            }
            // 读取图片
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            // 读取字节长度
            int len = 0;
            byte [] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                // 存储读取的字节数组
                data.write(bytes, 0, len);
            }
            // 获取字节数组
            byte[] baseByte = data.toByteArray();
            // 获取base64编码器
            Base64.Encoder encoder = Base64.getEncoder();
            // 将字节数组转换base64
            String encodeToString = encoder.encodeToString(baseByte);
//            System.out.println("======>>>>>>：" + pre + encodeToString);
            fileInputStream.close();
            return encodeToString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getDateFormat() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(timeZone);
        return df.format(calendar.getTime());
    }
    public static void main(String[] args) {
//        String path = "D:\\桌面1\\管网\\华为摄像机\\pic\\123.jpg";
        String path = "D:\\桌面1\\管网\\华为摄像机\\pic\\gw6.jpg";
        String s = base64Encoding(path);
//        System.out.println(s);
        HashMap<String, Object> map = new HashMap<>();
        map.put("DeviceID", "1234");
        map.put("Channel", "1");
        String format = getDateFormat();
        map.put("Time", format);
        map.put("ImageBase64", s);
        map.put("Name", "gw6.jpg");
//        System.out.println("发送数据---" + map);
        String url = "http://172.16.1.116:5001/detect";
        String s1 = HttpClientUtils.doPostJson(url, map);
        System.out.println("返回结果-s1--" + s1);
        JSONObject parse = (JSONObject) JSONObject.parse(s1, Feature.UseObjectArray);
        System.out.println("返回结果--parse-" + parse);
    }
}
