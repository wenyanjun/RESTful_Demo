package com.huawei.sdc.restful.activeregister;

import java.net.InetAddress;
import java.net.ServerSocket;

public class ActiveRegisterTCPServerDemo extends ActiveRegisterServerDemo {

    private ActiveRegisterTCPServerDemo(ServerSocket serverSocket, String natUrl) {
        this.serverSocket = serverSocket;
        this.natUrl = natUrl;
    }

    /* 主动注册TCP服务实例创建 */
    public static ActiveRegisterTCPServerDemo create(String host, int port, String natUrl){
        try {
            InetAddress address = InetAddress.getByName(host);
            ServerSocket serverSocket = new ServerSocket(port, 0, address);
            System.out.println("serverSocket" + serverSocket);
            return new ActiveRegisterTCPServerDemo(serverSocket, natUrl);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }
}
