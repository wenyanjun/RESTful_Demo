package com.huawei.sdc.restful.bean;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;


/**
 * {picSnapshotTimes=18446744072350230992, producerName=app_agent, alarmName=uuid, picSnapshotDstOffset=1,
 * snapshotTzone=8, algType=dog_detect, PTS=18446744072350230992, metaTypeMask=16, metaName=dog_detect,
 * panorama=[{"picAttr":{"picwigh":"small","piclen":"large"},"panoramaPic":""}],
 * commonAlarmType=1,
 * obj=[{"globalObjId":1,"closeUp":[{"picAttr":{"picwigh":"small","piclen":"large"},"closeUpPic":""}],
 * "objId":1,"objAttr":{"glass":"yes","hair":"yes"},"describeInfo":"ren","objPosR":{"x":3161,"width":3970,"y":2389,"height":4485}}],
 * alarmSource=Yolov3, channelId=101, metadataType=0}
 */
@Data
public class AlarmBean {
    // 告警源
    private String alarmSource;
    // 告警名称
    private String alarmName;
    // 告警类型
    private String commonAlarmType;
    // 目标对象数组 小图
    private JSONArray obj;
    // 截图时间
    private String picSnapshotTimes;
    private String producerName;
    private Integer picSnapshotDstOffset;
    private Integer snapshotTzone;
    private String algType;
    private String PTS;
    private String metaTypeMask;
    private String metaName;
    // 通道
    private String channelId;
    private String metadataType;
    // 目标对象 小图
    private JSONArray panorama;
}
