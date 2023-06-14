package com.huawei.sdc.restful.task;

import com.huawei.sdc.restful.activeregister.ActiveRegisterServerDemo;
import com.huawei.sdc.restful.activeregister.ActiveRegisterSyncWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class TaskService {

    // 设备保活 定时发送注册请求
//    @Scheduled(cron = "0 */1 * * * ? ")
    public void deviceActive() {
        log.info("定时任务执行...");
        List<ActiveRegisterSyncWorker> workers = ActiveRegisterServerDemo.workers;
        for (ActiveRegisterSyncWorker worker : workers) {
            try {
                log.info("worker---" + worker);
                worker.getSystemInfo();
                worker.setOsd();
            }catch (Exception e){
                log.info("设备出错..." + e.getMessage());
            }
        }
    }
}
