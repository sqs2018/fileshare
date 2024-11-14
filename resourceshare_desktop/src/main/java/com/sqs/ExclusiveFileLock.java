package com.sqs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class ExclusiveFileLock {



   /* public boolean acquireLock(String lockFilePath) {
        try {
            File file = new File(lockFilePath);
            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // 创建或打开一个文件
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            // 获取文件锁
            fileLock = raf.getChannel().tryLock();
            return fileLock != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void releaseLock() {
        if (fileLock != null) {
            try {
                fileLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/
    public static void main(String[] args) throws IOException {
        ExclusiveFileLock fileLockManager = new ExclusiveFileLock();
        String lockFilePath = "D:\\resources_exec\\data\\appLock\\appLaunch.lock";

        File file = new File(lockFilePath);
        // 确保父目录存在
       /* File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }*/
        // 创建或打开一个文件
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        // 获取文件锁
         FileLock  fileLock = raf.getChannel().tryLock();

        boolean lockAcquired = fileLock!=null;
        if (lockAcquired) {
            // 文件已独占，执行你的逻辑
            // ...
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 逻辑处理完成后释放锁
           // fileLockManager.releaseLock();
        } else {
            // 无法独占文件，可以选择退出或等待解锁
            System.out.println("Cannot acquire lock. Exiting...");
        }
    }
}