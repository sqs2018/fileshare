package com.sqs.frames;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.sqs.app.Launcher;
import com.sqs.components.RCButton;
import com.sqs.entity.File;
import com.sqs.panel.treedemo.Profession;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.ResourceChunk;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.util.HttpCallBack;
import com.sqs.util.HttpClientUtil;
import com.sqs.util.Md5Util;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/***
 * 分块验证对话框
 */
public class Md5ValidateDialog extends JDialog {


    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;

    private static final int dialogWidth = 700;
    private static final int dialogHeight = 400;
    private File file;
    List<ResourceChunk> chunkList;


    private RCButton validateButton;

    public Md5ValidateDialog(Frame frame, File file) {
        super(frame);
        this.file = file;
        initComponents();
        initView();
        centerScreen();
        initData();
    }


    private void initComponents() {
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        validateButton = new RCButton("开始验证", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
    }

    private void initView() {
        setLayout(new BorderLayout());
        setTitle("文件分块验证");
        Dimension windowSize = new Dimension(dialogWidth, dialogHeight);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);

        table.setRowHeight(40);// 设置表格的行高
        //table.setSelectionForeground(Color.red);// 设置选中的文字颜色
        jsp = new JScrollPane(table);

        tableModel = (DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"块号", "块大小", "块MD5值", "状态"});    //设置表头


        //table.getColumn("块号").setCellRenderer(new ImageAndTextTableCellRenderer());
        table.setModel(tableModel);    //应用表格模型
        jsp.setForeground(Colors.DARK);

        add(jsp, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(validateButton);
        add(panel, BorderLayout.NORTH);
        validateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                validateFile();
            }
        });
    }

    /**
     * 使窗口在屏幕中央显示
     */
    private void centerScreen() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - dialogWidth) / 2,
                (tk.getScreenSize().height - dialogHeight) / 2);
    }


    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Launcher.getServerUrl() + "/resource/queryResourceChunk/" + file.getId();
                HttpClientUtil.doGet(url, null, new HttpCallBack() {
                    @Override
                    public void response(String result) {
                        System.out.println("md5validate initData=====>" + result);
                        if (result != null) {
                            java.lang.reflect.Type resourceChunkListType = new TypeToken<LinkedList<ResourceChunk>>() {
                            }.getType();

                            //tableModel.setRowCount(0);
                            chunkList = new Gson().fromJson(result, resourceChunkListType);
                            if (chunkList != null) {
                                //数据排序
                                chunkList.sort(new Comparator<ResourceChunk>() {
                                    @Override
                                    public int compare(ResourceChunk o1, ResourceChunk o2) {
                                        return o1.getChunkNumber() > o2.getChunkNumber() ? 1 : -1;
                                    }
                                });
                                tableModel.setRowCount(0);    //清空表格中的数据
                                //"块号", "块大小", "块MD5值", "状态"
                                for (ResourceChunk resourceChunk : chunkList) {
                                    tableModel.addRow(new Object[]{resourceChunk.getChunkNumber(), resourceChunk.getCurrentChunkSize(), resourceChunk.getMd5(), "未开始"});
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
        }).start();

    }


    private void validateFile() {
        String filePath = file.getPath();
        tableModel.setRowCount(0);    //清空表格中的数据
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            for (int i=0;i<chunkList.size();i++) {
                ResourceChunk resourceChunk=chunkList.get(i);
                byte buf[]=new byte[resourceChunk.getCurrentChunkSize().intValue()];
                //fileInputStream.skip(i * Md5Util.CHUNKSIZE);
                int readSize=fileInputStream.read(buf);
                if(readSize==-1){
                    tableModel.addRow(new Object[]{resourceChunk.getChunkNumber(), resourceChunk.getCurrentChunkSize(), resourceChunk.getMd5(), "读取块为空,验证失败"});
                    //验证失败
                    JOptionPane.showMessageDialog(null, "验证不通过", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String md5= Md5Util.getMD5Bytes(buf);
                System.out.println("第"+(i+1)+"块 md5="+md5);
                if(md5.equals(resourceChunk.getMd5())){
                    tableModel.addRow(new Object[]{resourceChunk.getChunkNumber(), resourceChunk.getCurrentChunkSize(), resourceChunk.getMd5(), "验证通过"});
                }else{
                    tableModel.addRow(new Object[]{resourceChunk.getChunkNumber(), resourceChunk.getCurrentChunkSize(), resourceChunk.getMd5(), "Md5不一致,验证失败"});
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
