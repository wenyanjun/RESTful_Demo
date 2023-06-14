package com.huawei.sdc.restful.bean;

import lombok.Data;

@Data
public class AlarmBean {
    // 告警源
    private String alarmSource;
    // 告警名称
    private String alarmName;
    // 告警类型
    private String commonAlarmType;
    // 目标对象数组
    private Object obj;
}
