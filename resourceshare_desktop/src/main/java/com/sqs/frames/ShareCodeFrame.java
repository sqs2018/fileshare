package com.sqs.frames;

import com.google.gson.Gson;
import com.sqs.app.Launcher;
import com.sqs.components.*;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.User;
import com.sqs.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/***
 * 显示文件共享码和二维码
 */
public class ShareCodeFrame extends JFrame {
    private static final int windowWidth = 280;
    private static final int windowHeight = 350;


    private JPanel controlPanel;
    private JLabel closeLabel;
    private JPanel editPanel;
    private JLabel titleLabel;
    private JLabel userTypeLabel;

    private static Point origin = new Point();

    private MainFrame mainFrame;
    private String shareCode;

    public ShareCodeFrame(String shareCode) {
        this.shareCode = shareCode;
        initComponents();
        initView();
        centerScreen();
        setListeners();
        initData(shareCode);
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
        titleLabel.setText("文件共享码:" + shareCode);
        titleLabel.setFont(FontUtil.getDefaultFont(18));


        editPanel = new JPanel();
        editPanel.setLayout(new FlowLayout());

        Dimension textFieldDimension = new Dimension(200, 200);


        userTypeLabel = new JLabel();
        userTypeLabel.setPreferredSize(textFieldDimension);



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
        JLabel label = new JLabel("请使用本系统APP扫描二维码下载文件");
        titlePanel.add(label);


        editPanel.add(userTypeLabel);



        add(contentPanel);
        contentPanel.add(titlePanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 4).setInsets(10, 10, 0, 10));
        contentPanel.add(editPanel, new GBC(0, 2).setFill(GBC.HORIZONTAL).setWeight(1, 6));
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
                ShareCodeFrame.this.setVisible(false);
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
                    Point p = ShareCodeFrame.this.getLocation();
                    // 设置窗口的位置
                    ShareCodeFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
                            - origin.y);
                }
            });
        }
    }

    private void initData(String shareCode) {
        try {
            URL url = new URL(Launcher.getServerUrl() + "/file/generateQRCode/" + shareCode);
            BufferedImage image = ImageIO.read(url);
            userTypeLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
