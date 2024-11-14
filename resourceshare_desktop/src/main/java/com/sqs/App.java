package com.sqs;

import com.sqs.app.Launcher;

import java.io.IOException;

/**
 * Created by shiqs  2024-08-10
 * 系统启动类
 */
public class App {
    public static void main(String[] args) throws IOException {
        Launcher launcher = new Launcher(args);
        launcher.launch();
    }
}
