package com.sqs.app;

import com.sqs.App;
import com.sqs.frames.MainFrame;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Created by shiqs 2024-08-10
 */
public class Launcher {
    public static String APP_NAME = "面向多终端资源共享系统(电脑端)";
    private static Launcher context;

    public static String userHome;
    public static String appFilesBasePath;
    private static String serverUrl;
    // private static User

    private JFrame currentFrame;

    //通过选择文件，右击菜单上传文件
    private static String uploadFilePath = "";

    /***
     * 启动方式  包括源码启动和jar包启动。默认源码启动
     */
    private static String startMode = "SourceStart";


    public Launcher(String[] args) {
        context = this;
        if (args.length > 0) {
            uploadFilePath = args[0];
            startMode = "jarStart";
        }
    }

    public void launch() {
        // 设置系统配置信息
        config();
        // 判断是否启动过应用程序
        if (!isApplicationRunning()) {
            //启动本地服务，获取新实例传的参数,支持通过桌面右键快捷方式上传文件
            try {
                startLocalServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //展示界面
            openFrame();
            //执行上传文件
            uploadFile(uploadFilePath);
        } else {
            try {
                //向上一个程序实例传参
                Socket socket = new Socket("localhost", 8893);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(uploadFilePath);
                socket.close();
               TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }

    public static void uploadFile(String filePath) {
        MainFrame.getContext().getLeftPanel().clickMenu("fu_button");
        MainFrame.getContext().getRightPanel().setUploadPanelPath(filePath);
    }

    /****
     * 获取服务器IP
     *
     * @return
     */
    public static String getServerUrl() {
        return serverUrl;
    }

    private void openFrame() {
        currentFrame = new MainFrame();
        currentFrame.setVisible(true);
    }

    private void config() {
        userHome = System.getProperty("user.home");



        String workingDir =App.class.getResource("").getPath().replace("com/sqs/","");;
       // URL resource =System.getProperty("user.dir");
        ;
        //String workingDir = resource.getPath();
        if (startMode.equals("jarStart")) {
            workingDir = "D:\\resources_exec\\";
        }
        String configPath = workingDir + "config.properties";
		/*if(!new File(configPath).exists()){
			configPath = workingDir+ "config.properties";
		}*/
        appFilesBasePath = workingDir  + "data";
        // 读取配置文件
        //String configPath = workingDir+File.separator+ "config.properties";
        File configFile = new File(configPath);
        if (configFile.exists()) {
            try {
                Properties properties = new Properties();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));

                properties.load(bufferedReader);

                serverUrl = properties.getProperty("serverUrl");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            System.err.println(configPath + "配置文件未找到！！！！");
            System.exit(-1);
        }
        System.out.println(configPath);
    }

    private static void startLocalServer() throws Exception {
        System.out.println("正在启动本地服务");
        ServerSocket serverSocket = new ServerSocket(8893);
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("本地服务启动完成，等待新实例传输文件路径");
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String str = in.readLine();
                        System.out.println("接收到上传文件路径" + str);
                        in.close();
                        socket.close();
                        //执行文件上传
                        uploadFile(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
       // serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * 通过文件锁来判断程序是否正在运行
     *
     * @return 如果正在运行返回true，否则返回false
     */
    private static boolean isApplicationRunning() {
        boolean rv = false;
        try {
            String appFilesBasePath="D:\\resources_exec\\data";
            String path = appFilesBasePath + System.getProperty("file.separator") + "appLock";

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String lockFilePath=path + System.getProperty("file.separator") + "appLaunch.lock";
            System.out.println("程序文件锁路径"+lockFilePath);
            File lockFile = new File(lockFilePath);
           // File lockFile = new File("D:\\resources_exec\\data\\appLock\\appLaunch.lock");
            if (!lockFile.exists()) {
                lockFile.createNewFile();
            }


            // 创建或打开一个文件
            RandomAccessFile raf = new RandomAccessFile(lockFile, "rw");
            // 获取文件锁
            FileLock  fileLock = raf.getChannel().tryLock();
            // 程序名称
            if (fileLock == null||!fileLock.isValid()) {
                System.out.println("程序已在运行.");
                rv = true;
            }
            System.out.println("appLaunch.lock 文件已加锁");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rv;
    }

    public static Launcher getContext() {
        return context;
    }
}
