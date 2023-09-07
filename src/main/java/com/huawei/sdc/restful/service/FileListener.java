package com.huawei.sdc.restful.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileListener extends FileAlterationListenerAdaptor {
    /**
     * 文件创建执行
     */
    public void onFileCreate(File file) {
        log.info("[新建文件]:"+ file.getAbsolutePath());


//        File file1 = new File(file.getAbsolutePath());
//        file1.delete();
    }
    /**
     * 文件创建修改
     */
    public void onFileChange(File file) {
        log.info("[修改]:"+ file.getAbsolutePath());
    }
    /**
     * 文件删除
     */
    public void onFileDelete(File file) {
        log.info("[删除]:"+ file.getAbsolutePath());


    }
    /**
     * 目录创建
     */
    public void onDirectoryCreate(File directory) {
        log.info("[新建目录]:"+ directory.getAbsolutePath());
    }
    /**
     * 目录修改
     */
    public void onDirectoryChange(File directory) {
        log.info("[修改]:"+ directory.getAbsolutePath());
    }
    /**
     * 目录删除
     */
    public void onDirectoryDelete(File directory) {
        log.info("[删除]:"+ directory.getAbsolutePath());
    }
    public void onStart(FileAlterationObserver observer) {
// TODO Auto-generated method stub
        super.onStart(observer);
    }
    public void onStop(FileAlterationObserver observer) {
// TODO Auto-generated method stub
        super.onStop(observer);
    }
    public static void main(String[] args) throws Exception{
        // 监控目录
        String rootDir = "D:\\桌面1\\管网\\warn-pic";
        // 轮询间隔 5 秒
        long interval = TimeUnit.SECONDS.toMillis(5);
        // 创建过滤器
        IOFileFilter directories = FileFilterUtils.and(
                FileFilterUtils.directoryFileFilter(),
                HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".jpg")
        );
        IOFileFilter files1 = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter(".txt")
        );
        IOFileFilter filter = FileFilterUtils.or(directories, files, files1);
        // 使用过滤器
        FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir), filter);
        //不使用过滤器
//        FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir));
        observer.addListener(new FileListener());
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        monitor.start();
    }
}
