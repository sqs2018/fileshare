package com.sqs.frames;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.sqs.app.Launcher;
import com.sqs.components.RCButton;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.DownloadLog;
import com.sqs.resourceshare.entity.ResourceChunk;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.resourceshare.entity.User;
import com.sqs.util.HttpCallBack;
import com.sqs.util.HttpClientUtil;
import com.sqs.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 显示分享用户
 */
public class ShowUserFrame extends JFrame {
    private Long fileId;
    private static final int windowWidth = 600;
    private static final int windowHeight = 400;
    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;
    List<User> userList;
    private RCButton okButton = null;

    public ShowUserFrame(Long fileId) {
        this.fileId = fileId;
        initComponents();
        initView();
        centerScreen();
        initData(fileId);
    }


    private void initComponents() {
        Dimension windowSize = new Dimension(windowWidth, windowHeight);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);
        setTitle("分享给用户");
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void initView() {
        table.setRowHeight(40);// 设置表格的行高
        setLayout(new BorderLayout());
        //table.setSelectionForeground(Color.red);// 设置选中的文字颜色
        jsp = new JScrollPane(table);

        tableModel = (DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"序号", "用户"});    //设置表头
        table.setModel(tableModel);    //应用表格模
        add(jsp, BorderLayout.CENTER);

        okButton = new RCButton("确定", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        add(okButton, BorderLayout.SOUTH);

        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowIndex[] = table.getSelectedRows();
                if (rowIndex.length > 0) {
                    String toUser = "";
                    for (int i : rowIndex) {
                        toUser += userList.get(i).getUserName();
                        toUser += ",";
                    }
                    Map<String, String> params = new HashMap<>();
                    params.put("fileId", fileId.toString());
                    params.put("users", toUser);
                    params.put("shareUser", LoginFrame.Loginuser.getUserName());
                    //分享文件
                    HttpClientUtil.doPostJson(Launcher.getServerUrl() + "/file/shareFileToUser", new Gson().toJson(params), new HttpCallBack() {
                        @Override
                        public void response(String result) {
                            System.out.println("=====>" + result);
                            JOptionPane.showMessageDialog(null, "分享成功", "提示", JOptionPane.WARNING_MESSAGE);
                            ShowUserFrame.this.dispose();
                        }

                        @Override
                        public void error(String error) {
                            JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                        }
                    });


                }
            }
        });

    }

    private void centerScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - windowWidth) / 2, (tk.getScreenSize().height - windowHeight) / 2);
    }

    //初始化数据
    public void initData(Long fileId) {
        //v1/file/parent/0
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/getNormalUser/" + LoginFrame.Loginuser.getUserName(), null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                tableModel.setRowCount(0);
                userList = new Gson().fromJson(result, new TypeToken<List<User>>() {
                }.getType());
                if (userList != null) {
                    for (int i = 0; i < userList.size(); i++) {
                        tableModel.addRow(new Object[]{i + 1, userList.get(i).getUserName()});
                    }
                }
            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

    }
}
