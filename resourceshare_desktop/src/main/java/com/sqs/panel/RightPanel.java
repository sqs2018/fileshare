package com.sqs.panel;


import com.sqs.frames.MainFrame;
import com.sqs.res.Colors;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    private static RightPanel context;
    private JPanel centerPanel;


    // private JPanel contentPanel;

    private CardLayout cardLayout;

    private DownloadPanel downloadPanel;
    private UploadPanel uploadPanel;
    private PublicPanel publicPanel;
    private DownLoadListPanel downLoadListPanel;
    private PrivatePersonPanel privatePersonPanel;


    public RightPanel() {
        context = this;
        initComponents();
        initView();

    }


    private void initComponents() {
        cardLayout = new CardLayout();
        /*contentPanel = new JPanel();
        contentPanel.setLayout(cardLayout);*/

        downloadPanel = new DownloadPanel();
        uploadPanel = new UploadPanel();
        publicPanel = new PublicPanel();
        downLoadListPanel = new DownLoadListPanel();

        privatePersonPanel = new PrivatePersonPanel();
      /*  titlePanel = new TitlePanel(this);
        chatPanel = new ChatPanel(this);
        roomMembersPanel = new RoomMembersPanel(this);
        tipPanel = new TipPanel(this);
        userInfoPanel = new UserInfoPanel(this);
        webBroswerPanel = new WebBrowserPanel(this);*/
    }

    private void initView() {
        //this.setBackground(Colors.RED);
        setLayout(new BorderLayout());
        centerPanel = new JPanel();
        //centerPanel.setBackground(Colors.DARK);
        centerPanel.setLayout(cardLayout);
        add(centerPanel, BorderLayout.CENTER);
        centerPanel.add(downloadPanel, "downloadPanel");
        centerPanel.add(uploadPanel, "uploadPanel");
        centerPanel.add(publicPanel, "publicPanel");
        centerPanel.add(downLoadListPanel, "downLoadListPanel");
        centerPanel.add(privatePersonPanel, "privatePersonPanel");

        //  this.setPreferredSize(new Dimension(700, MainFrame.DEFAULT_HEIGHT));
    }

    public void showPanel(String who) {
        cardLayout.show(centerPanel, who);
        if (who.equals("publicPanel")) {
            publicPanel.initData(0);
        } else if (who.equals("uploadPanel")) {
            uploadPanel.initData(null);
        } else if (who.equals("privatePersonPanel")) {
            privatePersonPanel.initData(0);
        } else if (who.equals("downloadPanel")) {
            downloadPanel.initData();
        }
    }

    public void setUploadPanelPath(String filePath) {
        uploadPanel.setUploadFilePath(filePath);
    }

    public DownLoadListPanel getDownLoadListPanel() {
        return downLoadListPanel;
    }

    public static RightPanel getContext() {
        return context;
    }


}
