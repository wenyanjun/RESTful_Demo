# RESTful_Demo
> 技术博客 [http://idea.coderyj.com/](http://idea.coderyj.com/)
> 技术交流QQ群 `729987144`  `272712006`

#### 1.华为摄像机demo

摄像机NAT demo下载地址 下载路径见：https://bbs.huaweicloud.com/forum/thread-178101-1-1.html

但是此demo有bug  只能发送get请求 post put请求都不能发送,经过改造之后可以发post, put请求 实际上就是增加了请求体的长度`Content-Length`

```java
headers.addHeader("Content-Type", "application/json");
if (null != body){
    // 请求体带参数的要添加长度
    headers.addHeader("Content-Length", String.valueOf(body.length));
}
```

#### 2.第三方算法报警上报 但是华为摄像机发送的请求头 `Content-Type:application/` 这样spring boot post请求不能解析

- 报错 `Invalid mime type “application/“: does not contain subtype after ‘/‘`

- 解决办法 
```java
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
```

  