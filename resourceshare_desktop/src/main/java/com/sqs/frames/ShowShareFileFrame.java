package com.sqs.frames;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.sqs.app.Launcher;
import com.sqs.components.RCButton;
import com.sqs.components.table.ImageAndTextTableCellRenderer;
import com.sqs.entity.File;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.resourceshare.entity.User;
import com.sqs.util.HttpCallBack;
import com.sqs.util.HttpClientUtil;
import com.sqs.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 显示有人共享给当前用户对话框
 */
public class ShowShareFileFrame extends JFrame {
    private static final int windowWidth = 600;
    private static final int windowHeight = 400;
    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;
    List<LinkedTreeMap<String, Object>> data;
    private RCButton okButton = null;

    public ShowShareFileFrame(List<LinkedTreeMap<String, Object>> data) {
        this.data = data;
        initComponents();
        initView();
        centerScreen();
    }


    private void initComponents() {
        Dimension windowSize = new Dimension(windowWidth, windowHeight);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);
        setTitle("有人向您共享文件");
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        okButton = new RCButton("下载", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);

    }

    private void initView() {
        table.setRowHeight(40);// 设置表格的行高
        setLayout(new BorderLayout());
        //table.setSelectionForeground(Color.red);// 设置选中的文字颜色
        jsp = new JScrollPane(table);

        tableModel = (DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"序号", "分享用户", "文件名"});    //设置表头
        table.setModel(tableModel);    //应用表格模
        add(jsp, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);


        for (int i = 0; i < data.size(); i++) {
            LinkedTreeMap<String, Object> file = data.get(i);
            String fromUser = file.get("fromUser").toString();
            String fileName = file.get("fileName").toString();
            tableModel.addRow(new Object[]{i + 1, fromUser, fileName, file});
        }
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectIndex = table.getSelectedRow();
                if (selectIndex == -1) {
                    JOptionPane.showMessageDialog(null, "请选择下载文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, Object> fileMap = data.get(selectIndex);
                File file = new File();
                file.setId(Double.valueOf(fileMap.get("fileId").toString()).longValue());
                //查询文件信息
                HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/" + file.getId(), null, new HttpCallBack() {
                    @Override
                    public void response(String result) {
                        System.out.println("=====>" + result);
                        ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                        if (responseDto != null) {
                            if (responseDto.getMsg().equals("success")) {
                                Map fileMap =  (LinkedTreeMap<String, Object>) responseDto.getData();
                                file.setId(Double.valueOf(fileMap.get("id").toString()).longValue());
                                file.setType(fileMap.get("type").toString());
                                file.setFileName(fileMap.get("fileName").toString());
                                if ("folder".equals(file.getType())) {
                                    file.setSize(null);
                                } else {
                                    file.setSize(Double.valueOf(fileMap.get("size").toString()).longValue());
                                }
                                file.setDownloadProgress(0.0);
                                file.setStatue(1);
                                //1.加入下载列表
                                MainFrame.getContext().addDownLoadFile(file);
                                //2.跳转到下载列表界面
                                MainFrame.getContext().getLeftPanel().clickMenu("fdl_button");
                                ShowShareFileFrame.this.dispose();
                            }

                        }


                    }

                    @Override
                    public void error(String error) {
                        JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                    }
                });


            }
        });

    }

    private void centerScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - windowWidth) / 2, (tk.getScreenSize().height - windowHeight) / 2);
    }

}
