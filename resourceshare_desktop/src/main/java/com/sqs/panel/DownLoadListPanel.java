package com.sqs.panel;

import com.sqs.app.Launcher;
import com.sqs.components.RCButton;
import com.sqs.components.RCLabel;
import com.sqs.entity.File;
import com.sqs.frames.LoginFrame;
import com.sqs.frames.MainFrame;
import com.sqs.frames.Md5ValidateDialog;
import com.sqs.res.Colors;
import com.sqs.components.table.ImageAndTextTableCellRenderer;
import com.sqs.util.HttpCallBack;
import com.sqs.util.HttpClientUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * 下载列表界面
 */
public class DownLoadListPanel extends JPanel {


    //搜索panel
    private JPanel searchPanel;
    private RCButton searchButton;
    private RCButton md5ValidateButton;
    private RCButton openDirButton;//打开下载文件夹
    private JTextField rcTextField;

    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;
    private List<File> downLoadFiles = new ArrayList<>();//下载文件列表

    Md5ValidateDialog dialog=null;

    public DownLoadListPanel() {
        initComponents();
        initView();
        //
    }

    private void initComponents() {
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createLineBorder(Colors.SHADOW));
        Dimension textFieldDimension = new Dimension(200, 35);
        RCLabel label = new RCLabel("文件名称:");
        label.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(label);

        rcTextField = new JTextField();
        rcTextField.setPreferredSize(textFieldDimension);
        searchPanel.add(rcTextField);

        searchButton = new RCButton("搜索", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(searchButton);


        md5ValidateButton= new RCButton("文件分块验证", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(md5ValidateButton);


        openDirButton= new RCButton("打开下载文件夹", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(openDirButton);

        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


    }

    //更新文件下载进度
    public void updateProcess(File file) {
        for (int i = 0; i < downLoadFiles.size(); i++) {
            File temp = downLoadFiles.get(i);
            if (temp.getId().longValue() == file.getId().longValue()) {
                temp.setDownloadProgress(file.getDownloadProgress());
                if(file.getSize()!=null){
                    tableModel.setValueAt(file.getSizeStr(), i, 2);
                }
                tableModel.setValueAt((file.getDownloadProgress() * 100) + "%", i, 3);
                tableModel.setValueAt(file.getStatueStr(), i, 4);
                tableModel.fireTableRowsUpdated(i, i);
                return;
            }
        }

    }

    private void initView() {
        this.setLayout(new BorderLayout());

        // 用来获得鼠标点击位置的内容
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectRow = table.getSelectedRow();


            }
        });
        table.setRowHeight(40);// 设置表格的行高
       // table.setSelectionForeground(Color.red);// 设置选中的文字颜色
        jsp = new JScrollPane(table);

        tableModel = (DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"序号", "文件名", "大小", "进度", "状态"});    //设置表头


        table.getColumn("文件名").setCellRenderer(new ImageAndTextTableCellRenderer());
        table.setModel(tableModel);    //应用表格模型
        jsp.setForeground(Colors.DARK);


        add(searchPanel, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);


        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String fileName = rcTextField.getText();
                search(fileName);
            }
        });
        openDirButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //打开文件夹
                try {
                    String downloadPath = Launcher.appFilesBasePath + java.io.File.separator;
                    // 使用Desktop类来打开系统文件夹
                    Desktop.getDesktop().open(new java.io.File(downloadPath)); // 请替换为你想打开的文件夹路径
                } catch (IOException ex) {
                    ex.printStackTrace(); // 打印异常信息
                }

            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击
                if (e.getClickCount() == 2) {
                   /* if (files != null) {
                        int rowIndex = table.getSelectedRow();
                        initData(Double.valueOf(files.get(rowIndex).get("id").toString()).intValue());
                    }*/
                }
            }
        });
        //文件分块验证
        md5ValidateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowIndex = table.getSelectedRow();
                if(rowIndex!=-1){
                    File file= downLoadFiles.get(rowIndex);
                    java.io.File localFile=new java.io.File(file.getPath());
                    if(!localFile.exists()){
                        JOptionPane.showMessageDialog(null, "文件未找到，请重新下载后验证", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if(localFile.length()==0){
                        JOptionPane.showMessageDialog(null, "文件大小为0，无需验证", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if(file.getType().equals("folder")){
                        JOptionPane.showMessageDialog(null, "不支持文件夹验证", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    dialog=new Md5ValidateDialog(MainFrame.getContext(),file);
                    dialog.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(null, "请选择验证文件", "提示", JOptionPane.WARNING_MESSAGE);
                }

            }
        });
    }


    public void addDownLoadFile(File file) {
        //验证是否已在下载列表
        for (File temp : downLoadFiles) {
            if (temp.getId().longValue() == file.getId().longValue()) {
                JOptionPane.showMessageDialog(null, "文件已在下载列表", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        downLoadFiles.add(file);

        String loginuser=LoginFrame.Loginuser==null?"":LoginFrame.Loginuser.getUserName();
        //下载文件
        String fileUrl = null;
        if(file.getOwner()!=null){
            fileUrl=Launcher.getServerUrl() + "/personfile/" + file.getId() + "/download/"+file.getOwner()+"?downloadUser="+ loginuser;
        }else{
            fileUrl=Launcher.getServerUrl() + "/file/" + file.getId() + "/download?downloadUser="+ loginuser;;
        }

        String downloadPath = Launcher.appFilesBasePath + java.io.File.separator + file.getFileName();
        //如果是文件夹，下载的就是压缩包
        if (file.getType().equals("folder")) {
            downloadPath += ".rar";
        }
        //启动线程下载文件
        String finalDownloadPath = downloadPath;
        String finalFileUrl = fileUrl;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClientUtil.downloadFile(finalFileUrl, finalDownloadPath, new HttpCallBack() {
                    @Override
                    public void response(String result) {
                        double downloadProcess = 0;
                        if (result.equals("success")) {
                            downloadProcess = 1.0;
                            file.setPath(finalDownloadPath);
                            //打开文件夹
                            try {
                                // 使用Desktop类来打开系统文件夹
                                Desktop.getDesktop().open(new java.io.File(finalDownloadPath).getParentFile()); // 请替换为你想打开的文件夹路径
                            } catch (IOException ex) {
                                ex.printStackTrace(); // 打印异常信息
                            }
                        } else {
                            long downloadSize = Long.valueOf(result);//下载文件大小
                            //不是文件夹
                            if (!file.getType().equals("folder")) {
                                downloadProcess = downloadSize*1.0 / file.getSize();
                            }else{
                                file.setSize(downloadSize);
                            }
                        }
                        file.setDownloadProgress(downloadProcess);
                        if (downloadProcess >= 1.0) {
                            file.setStatue(4);
                        }
                        //下载进度
                        System.out.println("文件下载进度==========" + downloadProcess);
                        updateProcess(file);
                    }

                    @Override
                    public void error(String error) {
                        System.out.println(error);
                    }
                });
            }
        }).start();

        //重新展示
        initData();


    }

    /***
     * 搜索
     * @param query
     */
    public void search(String query) {
        tableModel.setRowCount(0);    //清空表格中的数据
        int index=1;
        for (int i = 0; i < downLoadFiles.size(); i++) {
            File file = downLoadFiles.get(i);
            if(file.getFileName().indexOf(query)!=-1){
                String type = file.getType();
                if (type.equals("folder")) {
                    //"序号","文件名", "大小","进度"， "状态"}
                    tableModel.addRow(new Object[]{index++, type + ":" + file.getFileName(), "-", (file.getDownloadProgress() * 100) + "%", file.getStatueStr()});
                    //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
                } else {
                    tableModel.addRow(new Object[]{index++, type + ":" + file.getFileName(), file.getSizeStr(), (file.getDownloadProgress() * 100) + "%", file.getStatueStr()});
                }
            }

        }




    }


    //初始化数据
    public void initData() {
        tableModel.setRowCount(0);    //清空表格中的数据
        for (int i = 0; i < downLoadFiles.size(); i++) {
            File file = downLoadFiles.get(i);
            String type = file.getType();
            if (type.equals("folder")) {
                //"序号","文件名", "大小","进度"， "状态"}
                tableModel.addRow(new Object[]{i + 1, type + ":" + file.getFileName(), "-", (file.getDownloadProgress() * 100) + "%", file.getStatueStr()});
                //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
            } else {
                tableModel.addRow(new Object[]{i + 1, type + ":" + file.getFileName(), file.getSizeStr(), (file.getDownloadProgress() * 100) + "%", file.getStatueStr()});
            }
        }

    }


}
