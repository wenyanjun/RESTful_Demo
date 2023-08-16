package com.huawei.sdc.restful.runner;

import com.huawei.sdc.restful.activeregister.ActiveRegisterServerDemo;
import com.huawei.sdc.restful.activeregister.ActiveRegisterTCPServerDemo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Runner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("socket 开始启动...");
                String ip = "172.16.1.136";
                String port = "8080";
                String natUrl = "nat/test";

                ActiveRegisterServerDemo server = ActiveRegisterTCPServerDemo.create(ip, Integer.parseInt(port), natUrl);
                System.out.println("----server" + server);
                new Thread(server).start();
            }
        }).start();
    }
}
