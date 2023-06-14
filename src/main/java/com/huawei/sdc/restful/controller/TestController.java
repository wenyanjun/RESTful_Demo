package com.huawei.sdc.restful.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("test1")
public class TestController {
    Integer data = 160;
    long start = 0;
    @GetMapping("test1")
    public void test1(){
        log.info("开始处理照片...");
        start = System.currentTimeMillis();

        for (int i = 0; i < 30; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        int curr = currIndex();
                        try {
                            log.info("当前处理照片-- " + curr);
                            test(curr);
                            if (curr == 0){
                                break;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }

    }
    private synchronized int currIndex(){
        data--;
        if (data <= 0){
            data = 0;
            long end = System.currentTimeMillis();
            log.info("执行时间--" + (end-start) + "ms");
        }
        return data;
    }
    private static void test(Integer i) throws Exception{
        log.info("水印输出开始...." + i);

        // 读取原图片信息 得到文件（本地图片）
        File srcImgFile = new File("D:\\桌面1\\管网\\123.jpg");
        //将文件对象转化为图片对象
        Image srcImg = ImageIO.read(srcImgFile);
        //获取图片的宽
        int srcImgWidth = srcImg.getWidth(null);
        //获取图片的高
        int srcImgHeight = srcImg.getHeight(null);
//        System.out.println("图片的宽:"+srcImgWidth);
//        System.out.println("图片的高:"+srcImgHeight);

        //创建一个URL对象,获取网络图片的地址信息（网络图片）
//        URL url = new URL("https://pngimg.com/distr/img/ukraine.png");
//        //将URL对象输入流转化为图片对象 (url.openStream()方法，获得一个输入流)
//        Image srcImg = ImageIO.read(url.openStream());
//        //获取图片的宽
//        int srcImgWidth = srcImg.getWidth(null);
//        //获取图片的高
//        int srcImgHeight = srcImg.getHeight(null);
//        System.out.println("图片的宽:"+srcImgWidth);
//        System.out.println("图片的高:"+srcImgHeight);

        BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        // 加水印
        //创建画笔
        Graphics2D g = bufImg.createGraphics();
        //绘制原始图片
        g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
        //-------------------------文字水印 start----------------------------
        //根据图片的背景设置水印颜色
        g.setColor(new Color(255,255,255,255));
        //设置字体  画笔字体样式为微软雅黑，加粗，文字大小为60pt
        g.setFont(new Font("微软雅黑", Font.ITALIC, 35));
        //水印内容
        String waterMarkContent="杆点名称:黄华线 设备名称:枪机 电池电量:58% 电池电压:12V 温度:28° 光伏电压:36V 索引值" + i;
        //设置水印的坐标(为原图片中间位置)
        int x=(srcImgWidth - getWatermarkLength(waterMarkContent, g)) / 2;
        int y=srcImgHeight / 2;
        //画出水印 第一个参数是水印内容，第二个参数是x轴坐标，第三个参数是y轴坐标
        g.drawString(waterMarkContent, 20, 50);
//        g.drawString(waterMarkContent, 20, 100);
        g.dispose();
        //-------------------------文字水印 end----------------------------
        //待存储的地址
        String tarImgPath="D:\\桌面1\\管网\\target\\123-target"+ i +".jpg";
        // 输出图片
        FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
        ImageIO.write(bufImg, "png", outImgStream);
        outImgStream.flush();
        outImgStream.close();
        System.out.println("照片叠加完成:" + i);
    }
    /**
     * 获取水印文字的长度
     * @param waterMarkContent
     * @param g
     * @return
     */
    public static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }
}
