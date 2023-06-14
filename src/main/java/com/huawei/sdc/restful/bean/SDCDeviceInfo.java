package com.huawei.sdc.restful.bean;

import lombok.Data;

import java.util.Map;
// { "DeviceName": "HoloSens SDC", "Manufacturer": "huawei", "DeviceType": "M2141-10-EI",
// "SerialNumber": "2102412733WMMA000132",
// "DeviceVersion": { "Software": "SDC 9.0.0.SPC303", "Uboot": "U-Boot 2020.01", "Kernel": "Linux 4.19.90", "Hardware": "A" } }
@Data
public class SDCDeviceInfo {
    // 设备名称
    private String deviceName;
    // 设备厂商
    private String manufacturer;
    // 设备类别/型号
    private String deviceType;
    // 设备序列号
    private String serialNumber;
    // 设备版本信息
    private Map<String,String> deviceVersion;
}
