package com.huawei.sdc.restful.utils;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

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
}
