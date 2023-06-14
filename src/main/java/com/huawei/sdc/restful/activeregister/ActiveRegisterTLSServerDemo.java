package com.huawei.sdc.restful.activeregister;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;

public class ActiveRegisterTLSServerDemo extends ActiveRegisterServerDemo {

    private ActiveRegisterTLSServerDemo(SSLServerSocket sslServerSocket, String natUrl) {
        this.serverSocket = sslServerSocket;
        this.natUrl = natUrl;
    }

    /* 主动注册TLS服务实例创建，证书请自行替换 */
    public static ActiveRegisterTLSServerDemo create(String host, int port, String natUrl){
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("d:/tmp/test.keystore"), "123456".toCharArray());
            kmf.init(keyStore, "123456".toCharArray());
            sslContext.init(kmf.getKeyManagers(), null, null);
            InetAddress address = InetAddress.getByName(host);
            SSLServerSocket socket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(port, 0, address);
            return new ActiveRegisterTLSServerDemo(socket, natUrl);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }
}
