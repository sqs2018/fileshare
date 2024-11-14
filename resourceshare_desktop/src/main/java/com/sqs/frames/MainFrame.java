package com.sqs.frames;


import com.sqs.app.Launcher;
import com.sqs.components.InitComponent;
import com.sqs.entity.File;
import com.sqs.panel.LeftPanel;
import com.sqs.panel.RightPanel;
import com.sqs.panel.TopPanel;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.User;
import com.sqs.util.FontUtil;
import com.sqs.util.IconUtil;
import com.sqs.util.OSUtil;
import com.sqs.util.ThreadPoolUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

import static com.sqs.app.Launcher.APP_NAME;


/**
 * Created by sqs on 2024-08-10
 * <p>
 * 程序主窗体
 * <p>
 * 实例化时初始化系统托盘
 */
public class MainFrame extends JFrame implements InitComponent {
    public static int DEFAULT_WIDTH = 1200;
    public static int DEFAULT_HEIGHT = 666;

    public int currentWindowWidth = DEFAULT_WIDTH;
    public int currentWindowHeight = DEFAULT_HEIGHT;

    //左边界面
    private LeftPanel leftPanel;
    //右边界面
    private RightPanel rightPanel;
    //顶端界面
    private TopPanel topPanel;

    private static MainFrame context;
    private Image normalTrayIcon; // 正常时的任务栏图标
    private Image emptyTrayIcon; // 闪动时的任务栏图标
    private TrayIcon trayIcon;
    //是否需要闪图标 -功能不确定要
    private boolean trayFlashing = false;


    private boolean firstShown = true;

    public MainFrame() {
        context = this;
        initResource();
        initialize();
    }

    public void initComponents() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setTitle(APP_NAME);

        UIManager.put("Label.font", FontUtil.getDefaultFont());
        UIManager.put("Panel.font", FontUtil.getDefaultFont());
        UIManager.put("TextArea.font", FontUtil.getDefaultFont());

        UIManager.put("Panel.background", Colors.WINDOW_BACKGROUND);
        UIManager.put("CheckBox.background", Colors.WINDOW_BACKGROUND);

        leftPanel = new LeftPanel(this);
        rightPanel = new RightPanel();
        topPanel = new TopPanel();
    }
    public LeftPanel getLeftPanel(){
        return leftPanel;
    }

    public RightPanel getRightPanel() {
        return rightPanel;
    }
    public void addDownLoadFile(File file){
        getRightPanel().getDownLoadListPanel().addDownLoadFile(file);//
    }
    public void initView() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        //关闭窗口自动退出程序
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(topPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        centerScreen();
    }
    public void hasLogin(User user){
        topPanel.setName(user.getUserName());
    }
    public void setListeners() {
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 显示主窗口
                setToFront();

                // 任务栏图标停止闪动
                if (trayFlashing) {
                    trayFlashing = false;
                    trayIcon.setImage(normalTrayIcon);
                }
                super.mouseClicked(e);
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                currentWindowWidth = (int) e.getComponent().getBounds().getWidth();
                currentWindowHeight = (int) e.getComponent().getBounds().getHeight();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                if (firstShown) {
                    firstShown = false;
                }
                super.componentShown(e);
            }
        });




    }

    private void initResource() {
        ThreadPoolUtils.execute(() -> initTray());
    }


    /**
     * 初始化系统托盘图标
     */
    private void initTray() {
        SystemTray systemTray = SystemTray.getSystemTray();//获取系统托盘
        try {
            if (OSUtil.isMacOS()) {
                normalTrayIcon = IconUtil.getIcon(this, "/image/ic_launcher_dark.png", 20, 20, false).getImage();
                trayIcon = new TrayIcon(normalTrayIcon, APP_NAME);
                trayIcon.setImageAutoSize(true);

                trayIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        // 显示主窗口
                        setToFront();
                        super.mousePressed(e);
                    }
                });
            } else {
                normalTrayIcon = IconUtil.getIcon(this, "/image/ic_launcher.png", 20, 20, true).getImage();
                emptyTrayIcon = IconUtil.getIcon(this, "/image/ic_launcher_empty.png", 20, 20, true).getImage();

                trayIcon = new TrayIcon(normalTrayIcon, APP_NAME);
                trayIcon.setImageAutoSize(true);
                PopupMenu menu = new PopupMenu();


                MenuItem exitItem = new MenuItem("退出");
                exitItem.addActionListener(e -> exitApp());

                MenuItem showItem = new MenuItem("打开" + APP_NAME);
                showItem.addActionListener(e -> setToFront());
                menu.add(showItem);
                menu.add(exitItem);
                trayIcon.setPopupMenu(menu);
            }

            systemTray.add(trayIcon);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void exitApp() {
        System.exit(1);
    }


    /**
     * 设置任务栏图标闪动
     */
    public void setTrayFlashing() {
        trayFlashing = true;
        new Thread(() ->
        {
            while (trayFlashing) {
                try {
                    trayIcon.setImage(emptyTrayIcon);
                    Thread.sleep(800);

                    trayIcon.setImage(normalTrayIcon);
                    Thread.sleep(800);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public boolean isTrayFlashing() {
        return trayFlashing;
    }


    public static MainFrame getContext() {
        return context;
    }


    /**
     * 使窗口在屏幕中央显示
     */
    private void centerScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - currentWindowWidth) / 2,
                (tk.getScreenSize().height - currentWindowHeight) / 2);
    }


    @Override
    public void dispose() {
        // 移除托盘图标
        SystemTray.getSystemTray().remove(trayIcon);
        super.dispose();
    }

    /**
     * 使主窗口在最前面
     */
    public void setToFront() {
        setVisible(true);
        setAlwaysOnTop(true);
        setExtendedState(Frame.NORMAL);
        new Thread(() ->
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            setAlwaysOnTop(false);
        }).start();
    }

}

