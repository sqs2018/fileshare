package com.sqs.panel;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sqs.app.Launcher;
import com.sqs.components.GBC;
import com.sqs.components.RCButton;
import com.sqs.components.RCLabel;
import com.sqs.entity.File;
import com.sqs.frames.MainFrame;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/***
 * 文件下载界面
 */
public class DownloadPanel extends JPanel {


    private RCLabel label;
    //显示下载排行
    private JPanel topPanel;
    private JPanel downloadPanel;

    //下载排行
    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;
    private List<LinkedTreeMap<String, Object>> files;//返回数据

    //输入文件下载编码
    private JTextField num_tf;
    private RCButton download_button;

    public DownloadPanel() {
        initComponents();
        initView();
        initData();
    }



    private void initComponents() {

        label = new RCLabel("文件共享码:");
        num_tf = new JTextField();
        download_button = new RCButton("下载", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);

        topPanel=new JPanel();
        //topPanel.setBackground(Color.red);
        topPanel.setPreferredSize(new Dimension(450,400));

        downloadPanel=new JPanel();


        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void initView() {

        // this.setPreferredSize(new Dimension(300, 400));

        this.setLayout(new GridBagLayout());

        add(topPanel,new GBC(0,0));



        tableModel = (DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"序号", "文件名", "文件共享码","下载次数"});    //设置表头
        table.setModel(tableModel);
        table.setRowHeight(40);// 设置表格的行高
        //table.setSelectionForeground(Color.red);// 设置选中的文字颜色
        jsp = new JScrollPane(table);

        JLabel jLabel=new JLabel("下载排行");
        topPanel.add(jLabel,new GBC(0,0));
        topPanel.add(jsp,new GBC(0,1));


        downloadPanel.setLayout(new GridBagLayout());
        add(downloadPanel,new GBC(0,1).setInsets(100,10,10,0));
        downloadPanel.add(label, new GBC(0, 1).setInsets(10, 10, 0, 10));
        label.setPreferredSize(new Dimension(100, 30));
        downloadPanel.add(num_tf, new GBC(1, 1).setInsets(10, 10, 0, 10));
        num_tf.setPreferredSize(new Dimension(150, 30));
        downloadPanel.add(download_button, new GBC(2, 1).setInsets(10, 10, 0, 10));

        //setBorder(BorderFactory.createLineBorder(Colors.FONT_GRAY));

        // setBackground(Color.BLACK);
        download_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String shareCode = num_tf.getText();
                if (shareCode == null || shareCode.length() != 3) {
                    JOptionPane.showMessageDialog(null, "文件共享码为三位数字", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                downLoadFile(shareCode);
            }
        });



    }

    private void downLoadFile(String shareCode) {
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/queryByShareCode/" + shareCode, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                if (result == null || result.length() == 0 || result.equals("error")) {
                    JOptionPane.showMessageDialog(null, "对应文件共享码不存在", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto.getMsg().equals("success")) {
                    LinkedTreeMap<String, Object> fileMap = (LinkedTreeMap<String, Object>) responseDto.getData();
                    File file = new File();
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
                }else{
                    JOptionPane.showMessageDialog(null, "对应文件共享码不存在", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }


            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "获取文件失败：" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public void initData() {
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/selectDownloadTop", null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                tableModel.setRowCount(0);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            tableModel.addRow(new Object[]{ i+1,file.get("fileName"), file.get("shareCode"),file.get("size").toString().replace(".0","")});
                            //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());

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
