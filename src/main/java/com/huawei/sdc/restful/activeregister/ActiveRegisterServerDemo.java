package com.huawei.sdc.restful.activeregister;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public abstract class ActiveRegisterServerDemo implements Runnable {
    public static List<ActiveRegisterSyncWorker> workers = new ArrayList<ActiveRegisterSyncWorker>();
    protected ServerSocket serverSocket;
    protected String natUrl = "";

    public int port() {
        return serverSocket.getLocalPort();
    }

    public void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    /* 主动注册服务监听线程主循环 */
    public void run() {
        log.info("server started on port: " + port());

        try {
            Socket acceptSocket;
            while ((acceptSocket =  serverSocket.accept()) != null) {
                log.info("accepted");
                ActiveRegisterSyncWorker worker = new ActiveRegisterSyncWorker(acceptSocket);

                /*
                 *  接口调用应在完成主动注册交互之后，因此这里建议采用同步处理，先处理完主动注册请求并将其添加到设备列表，
                 *  后续再基于设备标识（如SN号）从设备列表中查询到相应设备的连接，进行接口调用。
                 * { "DeviceName": "HoloSens SDC", "Manufacturer": "huawei", "DeviceType": "M2141-10-EI", "SerialNumber": "2102412733WMMA000132",
                 * "DeviceVersion": { "Software": "SDC 9.0.0.SPC303", "Uboot": "U-Boot 2020.01", "Kernel": "Linux 4.19.90", "Hardware": "A" } }
                 */
                worker.dealWithActiveRegisterReq(this.natUrl);
                // 注册上来必须调用一下get请求
                worker.getSystemInfo();
                workers.add(worker);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("server stopped");
    }

    /* 根据设备SN号查询设备 */
    public static ActiveRegisterSyncWorker getDeviceWorker(String sn) {
        for (ActiveRegisterSyncWorker worker: workers) {
            if (sn.equals(worker.getDeviceInfo().getSerialNumber())) {
                return worker;
            }
        }
        return null;
    }

    /* 创建注册服务端实例，基类不提供具体实现. */
    public static ActiveRegisterServerDemo create(String host, int port, String natUrl) {
        return null;
    }
}
