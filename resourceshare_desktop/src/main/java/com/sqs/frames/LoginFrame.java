package com.sqs.frames;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sqs.app.Launcher;
import com.sqs.components.*;
import com.sqs.entity.File;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.DownloadLog;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.resourceshare.entity.User;
import com.sqs.util.*;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by shiqs
 */
public class LoginFrame extends JFrame {
    private static final int windowWidth = 300;
    private static final int windowHeight = 300;

    private static LoginFrame context;
    public static User Loginuser;

    private JPanel controlPanel;
    private JLabel closeLabel;
    private JPanel editPanel;
    private RCTextField usernameField;
    private RCPasswordField passwordField;
    private RCButton loginButton;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JLabel userTypeLabel;

    private static Point origin = new Point();

    private MainFrame mainFrame;
    private String username;

    public LoginFrame(MainFrame mainFrame) {
        context = this;
        this.mainFrame = mainFrame;
        initComponents();
        initView();
        centerScreen();
        setListeners();
    }

    private void initComponents() {
        Dimension windowSize = new Dimension(windowWidth, windowHeight);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);


        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        //controlPanel.setBounds(0,5, windowWidth, 30);

        closeLabel = new JLabel();
        closeLabel.setIcon(IconUtil.getIcon(this, "/image/close.png", false));
        closeLabel.setHorizontalAlignment(JLabel.CENTER);
        //closeLabel.setPreferredSize(new Dimension(30,30));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        titleLabel = new JLabel();
        titleLabel.setText("登  录");
        titleLabel.setFont(FontUtil.getDefaultFont(16));


        editPanel = new JPanel();
        editPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));

        Dimension textFieldDimension = new Dimension(200, 35);
        usernameField = new RCTextField();
        usernameField.setPlaceholder("用户名");
        usernameField.setPreferredSize(textFieldDimension);
        usernameField.setFont(FontUtil.getDefaultFont(14));
        usernameField.setForeground(Colors.FONT_BLACK);

        passwordField = new RCPasswordField();
        passwordField.setPreferredSize(textFieldDimension);
        passwordField.setPlaceholder("密码");
        //passwordField.setBorder(new RCBorder(RCBorder.bottom, Colors.LIGHT_GRAY));
        passwordField.setFont(FontUtil.getDefaultFont(14));
        passwordField.setForeground(Colors.FONT_BLACK);
        passwordField.setMargin(new Insets(0, 15, 0, 0));


        userTypeLabel = new JLabel();
        userTypeLabel.setText("用户类型：普通用户");
        userTypeLabel.setPreferredSize(textFieldDimension);

        loginButton = new RCButton("登 录", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        loginButton.setFont(FontUtil.getDefaultFont(14));
        loginButton.setPreferredSize(new Dimension(200, 40));

        statusLabel = new JLabel();
        statusLabel.setForeground(Colors.RED);
        statusLabel.setText("密码不正确");
        statusLabel.setVisible(false);

        setIconImage(IconUtil.getIcon(this, "/image/ic_launcher.png", true).getImage());
    }

    private void initView() {
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new LineBorder(Colors.LIGHT_GRAY));
        contentPanel.setLayout(new GridBagLayout());

        controlPanel.add(closeLabel);

        if (OSUtil.getOsType() != OSUtil.MacOS) {
            setUndecorated(true);
            contentPanel.add(controlPanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1).setInsets(5, 0, 0, 0));
        }

        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(loginButton, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setInsets(10, 0, 10, 0));

        editPanel.add(usernameField);
        editPanel.add(passwordField);

        editPanel.add(userTypeLabel);

        editPanel.add(buttonPanel);

        editPanel.add(statusLabel);


        add(contentPanel);
        contentPanel.add(titlePanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1).setInsets(10, 10, 0, 10));
        contentPanel.add(editPanel, new GBC(0, 2).setFill(GBC.BOTH).setWeight(1, 10).setInsets(10, 10, 0, 10));
    }

    /**
     * 使窗口在屏幕中央显示
     */
    private void centerScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - windowWidth) / 2,
                (tk.getScreenSize().height - windowHeight) / 2);
    }

    private void setListeners() {
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //System.exit(1);
                LoginFrame.this.setVisible(false);
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setBackground(Colors.LIGHT_GRAY);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setBackground(Colors.WINDOW_BACKGROUND);
                super.mouseExited(e);
            }
        });

        if (OSUtil.getOsType() != OSUtil.MacOS) {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    // 当鼠标按下的时候获得窗口当前的位置
                    origin.x = e.getX();
                    origin.y = e.getY();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    // 当鼠标拖动时获取窗口当前位置
                    Point p = LoginFrame.this.getLocation();
                    // 设置窗口的位置
                    LoginFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
                            - origin.y);
                }
            });
        }

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (loginButton.isEnabled()) {
                    doLogin();
                }

                super.mouseClicked(e);
            }
        });

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
        usernameField.addKeyListener(keyListener);
        passwordField.addKeyListener(keyListener);
    }

    private void doLogin() {
        String name = usernameField.getText();
        String pwd = new String(passwordField.getPassword());

        if (name == null || name.isEmpty()) {
            showMessage("请输入用户，注意首字母可能为大写");
        } else if (pwd == null || pwd.isEmpty()) {
            showMessage("请输入密码");
        } else {
            statusLabel.setVisible(false);
            loginButton.setEnabled(false);
            usernameField.setEditable(false);
            passwordField.setEditable(false);
            User user=new User(name,pwd,0);
            /*Map<String, String> params = new HashMap<>();
            params.put("username", name);
            params.put("password", pwd);
            //普通用户
            params.put("type", "0");*/

            HttpClientUtil.doPostJson(Launcher.getServerUrl() + "/loginClient", new Gson().toJson(user), new HttpCallBack() {
                @Override
                public void response(String result) {
                    System.out.println("登录返回======" + result);
                    if (result != null) {
                        if (result.indexOf("error") != -1) {
                            JOptionPane.showMessageDialog(null, result, "提示", JOptionPane.WARNING_MESSAGE);
                            statusLabel.setVisible(true);
                            loginButton.setEnabled(true);
                            usernameField.setEditable(true);
                            passwordField.setEditable(true);
                            return;
                        }
                        Loginuser = new Gson().fromJson(result, User.class);
                        mainFrame.hasLogin(Loginuser);
                        //登录成功
                        JOptionPane.showMessageDialog(null, "登录成功", "提示", JOptionPane.WARNING_MESSAGE);
                        //启动线程查询分享记录
                        new java.util.Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                System.out.println("-----定时查询分享记录-----");
                                queryShareFile();
                            }
                        },5000,3000);


                        LoginFrame.this.dispose();
                    }
                }

                @Override
                public void error(String error) {
                    JOptionPane.showMessageDialog(null, "服务器访问异常:" + error, "提示", JOptionPane.WARNING_MESSAGE);
                }
            });
        }


    }


    private void showMessage(String message) {
        if (!statusLabel.isVisible()) {
            statusLabel.setVisible(true);
        }

        statusLabel.setText(message);
    }

    public static LoginFrame getContext() {
        return context;
    }

    /****
     *  验证用户登录
     * @return
     */
    public static boolean validateLogin() {
        if(Loginuser!=null){
            return true;
        }
        int result = JOptionPane.showConfirmDialog(null, "未登录系统,是否登录系统！", "提示信息",
                JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            if (context == null) {
                context = new LoginFrame(MainFrame.getContext());
            }
            context.setVisible(true);
            return false;
        }
        return false;
    }
    private void queryShareFile(){
        String url=Launcher.getServerUrl() +"/file/polling/"+Loginuser.getUserName();
        HttpClientUtil.doGet(url, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("查询分享文件返回======" + result);
                if(result!=null){
                    ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                    if(responseDto.getData()!=null){
                        System.out.println("返回======" + responseDto.getData());
                        List<LinkedTreeMap<String, Object>> data= (List<LinkedTreeMap<String, Object>>)responseDto.getData();
                       // JOptionPane.showMessageDialog(null, "有人向您分享文件", "提示", JOptionPane.WARNING_MESSAGE);
                        ShowShareFileFrame showShareFileFrame=new ShowShareFileFrame(data);
                        showShareFileFrame.setVisible(true);
                    }
                }

            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "服务器访问异常:" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

}
