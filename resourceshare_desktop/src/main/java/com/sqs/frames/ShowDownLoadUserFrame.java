package com.sqs.frames;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sqs.app.Launcher;
import com.sqs.entity.File;
import com.sqs.resourceshare.entity.DownloadLog;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.util.HttpCallBack;
import com.sqs.util.HttpClientUtil;
import com.sqs.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ShowDownLoadUserFrame  extends JFrame {
    private Long fileId;
    private static final int windowWidth = 600;
    private static final int windowHeight = 400;
    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;
    private List<LinkedTreeMap<String, Object>> files;//返回数据
    public ShowDownLoadUserFrame(Long fileId){
        this.fileId=fileId;
        initComponents();
        initView();
        centerScreen();
        initData(fileId);
    }



    private void initComponents() {
        Dimension windowSize = new Dimension(windowWidth, windowHeight);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);
        setTitle("下载用户列表");
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
        tableModel.setColumnIdentifiers(new Object[]{"序号", "下载用户", "下载时间"});    //设置表头
        table.setModel(tableModel);    //应用表格模
        add(jsp, BorderLayout.CENTER);

    }
    private void centerScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - windowWidth) / 2, (tk.getScreenSize().height - windowHeight) / 2);
    }

    //初始化数据
    public void initData(Long fileId) {
        //v1/file/parent/0
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/downloadLog/" + fileId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                tableModel.setRowCount(0);
                ResponseDto<java.util.List<DownloadLog>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {

                        files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            Object userNameT=file.get("userName");
                            tableModel.addRow(new Object[]{i + 1, (userNameT==null||userNameT.equals(""))?"匿名用户":userNameT.toString(), StringUtils.formatDate(file.get("createTime").toString())});
                        }

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
