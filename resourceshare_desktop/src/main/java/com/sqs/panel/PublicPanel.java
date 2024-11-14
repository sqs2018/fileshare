package com.sqs.panel;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sqs.app.Launcher;
import com.sqs.components.RCButton;
import com.sqs.components.RCLabel;
import com.sqs.entity.File;
import com.sqs.frames.MainFrame;
import com.sqs.frames.ShareCodeFrame;
import com.sqs.frames.ShowDownLoadUserFrame;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.components.table.ImageAndTextTableCellRenderer;
import com.sqs.util.HttpCallBack;
import com.sqs.util.HttpClientUtil;
import com.sqs.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/***
 * 公共文件-不带分页
 */
public class PublicPanel extends JPanel {


    //搜索panel
    private JPanel searchPanel;

    private JPanel showPathPanel;
    private RCButton searchButton;
    private JTextField rcTextField;
    private RCButton downloadButton = null;
    private RCButton preButton = null;
    private RCButton delButton = null;

    private RCButton shareButton = null;

    private RCButton createFolderButton = null;


    private RCButton showDownloadUserButton = null;
    private JTable table;
    private JScrollPane jsp;
    private DefaultTableModel tableModel;
    private List<LinkedTreeMap<String, Object>> files;//返回数据
    private int currentFileId = 0;//当前目录Id

    private JLabel currentPathLabel;

    private Stack<Long> pathStack = new Stack<>();


    public PublicPanel() {
        initComponents();
        initView();
        //initData();
    }

    private void initComponents() {
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createLineBorder(Colors.SHADOW));


        preButton = new RCButton("上一级", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(preButton, FlowLayout.LEFT);

        Dimension textFieldDimension = new Dimension(200, 35);
        RCLabel label = new RCLabel("文件名称:");
        label.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(label);

        rcTextField = new JTextField();
        rcTextField.setPreferredSize(textFieldDimension);
        searchPanel.add(rcTextField);

        searchButton = new RCButton("搜索", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(searchButton);


        downloadButton = new RCButton("下载", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        downloadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        delButton = new RCButton("删除", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);

        searchPanel.add(downloadButton);
        searchPanel.add(delButton);

        shareButton = new RCButton("共享", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(shareButton);


        createFolderButton = new RCButton("新建文件夹", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(createFolderButton);


        showDownloadUserButton= new RCButton("显示下载用户", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        searchPanel.add(showDownloadUserButton);

        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        currentPathLabel = new JLabel("当前路径：/");
        showPathPanel = new JPanel();
        showPathPanel.setPreferredSize(new Dimension(600, 30));
        showPathPanel.add(currentPathLabel);
    }

    private void initView() {

        // this.setPreferredSize(new Dimension(300, 400));

        this.setLayout(new BorderLayout());

        // 用来获得鼠标点击位置的内容

        table.setRowHeight(40);// 设置表格的行高
        //table.setSelectionForeground(Color.red);// 设置选中的文字颜色
        jsp = new JScrollPane(table);

        tableModel = (DefaultTableModel) table.getModel();    //获得表格模型
        tableModel.setRowCount(0);    //清空表格中的数据
        tableModel.setColumnIdentifiers(new Object[]{"序号", "文件名", "大小", "修改日期", "共享码"});    //设置表头


        table.getColumn("文件名").setCellRenderer(new ImageAndTextTableCellRenderer());
        //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
        //table.setRowHeight(30);
        table.setModel(tableModel);    //应用表格模型
        // table.setVisible(false);
        //jsp.setForeground(Colors.DARK);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);

        add(searchPanel, BorderLayout.NORTH);
        add(jsp, BorderLayout.CENTER);

        add(showPathPanel, BorderLayout.SOUTH);

        // this.setBackground(Colors.BLUE);

        //System.out.println("====大小"+this.getParent().getPreferredSize());
      /*  this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                jsp.setPreferredSize(PublicPanel.this.getPreferredSize());
                super.componentResized(e);
            }
        });
*/
        //add(table,BorderLayout.CENTER);
        //addData();
      /*  add(label,new GBC(0,0).setInsets(10, 10, 0, 10));
        label.setPreferredSize(new Dimension(100, 30));
        add(num_tf,new GBC(1,0).setInsets(10, 10, 0, 10));
        num_tf.setPreferredSize(new Dimension(150, 30));
        add(download_button,new GBC(2,0).setInsets(10, 10, 0, 10));*/

        //setBorder(BorderFactory.createLineBorder(Colors.FONT_GRAY));

        // setBackground(Color.BLACK);
        //下载
        downloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // super.mouseClicked(e);
                int selectRow = table.getSelectedRow();
                if (selectRow < 0) {
                    JOptionPane.showMessageDialog(null, "请选择下载文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LinkedTreeMap<String, Object> fileMap = files.get(selectRow);
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
            }
        });
        delButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectRow = table.getSelectedRow();
                if (selectRow < 0) {
                    JOptionPane.showMessageDialog(null, "请选择删除文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String fileId = files.get(selectRow).get("id").toString();
                del(Double.valueOf(fileId).intValue());
            }
        });

        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String fileName = rcTextField.getText();
                search(fileName);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击
                if (e.getClickCount() == 2) {
                    if (files != null) {
                        int rowIndex = table.getSelectedRow();
                        if ("folder".equals(files.get(rowIndex).get("type").toString())) {
                            Long fileId = Double.valueOf(files.get(rowIndex).get("id").toString()).longValue();
                            Long parentId = Double.valueOf(files.get(rowIndex).get("parentId").toString()).longValue();
                            pathStack.push(parentId);
                            String path = currentPathLabel.getText();
                            if (path.endsWith("/")) {
                                currentPathLabel.setText(currentPathLabel.getText() + files.get(rowIndex).get("fileName").toString());
                            } else {
                                currentPathLabel.setText(currentPathLabel.getText() + "/" + files.get(rowIndex).get("fileName").toString());
                            }

                            initData(fileId.intValue());
                        }
                    }
                }
            }
        });
        preButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                //super.mouseClicked(e);
                if (pathStack.size() > 0) {
                    int fileId = pathStack.pop().intValue();


                    String path = currentPathLabel.getText();
                    int index = path.lastIndexOf("/");
                    if (index != -1) {
                        String subPath = path.substring(0, index);
                        if (subPath.indexOf("/") == -1) {
                            subPath += "/";
                        }
                        currentPathLabel.setText(subPath);
                    }
                    initData(fileId);
                } else {
                    initData(0);
                }

            }
        });
        shareButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectRow = table.getSelectedRow();
                if (selectRow < 0) {
                    JOptionPane.showMessageDialog(null, "请选择共享文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String fileId = String.valueOf(Double.valueOf(files.get(selectRow).get("id").toString()).longValue());
                shareFile(fileId);
            }
        });

        createFolderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String input = JOptionPane.showInputDialog("请输入文件夹名称：");
                if (input != null) {
                    createFolder(input);
                }
            }
        });

        showDownloadUserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectRow = table.getSelectedRow();
                if (selectRow < 0) {
                    JOptionPane.showMessageDialog(null, "请选择查询文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String fileId = String.valueOf(Double.valueOf(files.get(selectRow).get("id").toString()).longValue());
                ShowDownLoadUserFrame showDownLoadUserFrame=new ShowDownLoadUserFrame(Long.valueOf(fileId));
                showDownLoadUserFrame.setVisible(true);
            }
        });

    }

    private void createFolder(String folder) {
        File file = new File();
        file.setFileName(folder);
        file.setParentId(Long.valueOf(currentFileId));
        file.setType("folder");
        file.setCreateTime(new Date());
        //file.setOwner(LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName());
        file.setUpdateTime(new Date());
        //add-09-25
        HttpClientUtil.doPostJson(Launcher.getServerUrl() + "/file/createFolder", new Gson().toJson(file), new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto responseDto= new Gson().fromJson(result, ResponseDto.class);
                if(result!=null&&"success".equals(responseDto.getMsg())){
                    initData(currentFileId);
                }else {
                    JOptionPane.showMessageDialog(null, responseDto.getMsg(), "提示", JOptionPane.WARNING_MESSAGE);
                }

            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });


    }

    /***
     * 搜索
     * @param query
     */
    public void search(String query) {
        if (query.isEmpty()) {
            initData(0);
            return;
        }
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/query/" + currentFileId + "/" + query, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                tableModel.setRowCount(0);
                files = null;
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            String type = file.get("type").toString();
                            String shareCode = file.get("shareCode") == null ? "" : file.get("shareCode").toString();
                            if (type.equals("folder")) {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), "-", file.get("updateTime"), shareCode});
                                //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
                            } else {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"),  StringUtils.getFileSize(Double.valueOf(file.get("size").toString())), file.get("updateTime"), shareCode});
                            }
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

    /***
     * 删除
     * @param parentId
     */
    public void del(int parentId) {
        //v1/file/parent/0
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/del/" + parentId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        JOptionPane.showMessageDialog(null, "删除成功", "提示", JOptionPane.WARNING_MESSAGE);
                        //initData(currentFileId);
                        //重新查询
                        search(rcTextField.getText());
                    }

                }


            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });


    }

    /***
     * 共享
     */
    public void shareFile(String fileId) {
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/share/" + fileId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        // JOptionPane.showMessageDialog(null, "共享成功", "提示", JOptionPane.WARNING_MESSAGE);
                        //initData(currentFileId);
                        //重新查询
                        ShareCodeFrame shareCodeFrame = new ShareCodeFrame(responseDto.getData().toString());
                        shareCodeFrame.setVisible(true);
                        initData(currentFileId);
                    }

                }


            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    //初始化数据
    public void initData(int parentId) {
        currentFileId = parentId;
        //v1/file/parent/0
        HttpClientUtil.doGet(Launcher.getServerUrl() + "/file/parent/" + parentId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                tableModel.setRowCount(0);
                files = null;
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {

                        files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            String type = file.get("type").toString();
                            String shareCode = file.get("shareCode") == null ? "" : file.get("shareCode").toString();
                            if (type.equals("folder")) {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), "-", StringUtils.formatDate(file.get("updateTime").toString()), shareCode});
                                //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
                            } else {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), StringUtils.getFileSize(Double.valueOf(file.get("size").toString())), StringUtils.formatDate(file.get("updateTime").toString()), shareCode});
                            }
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
