package com.sqs.panel;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sqs.app.Launcher;
import com.sqs.components.GBC;
import com.sqs.components.RCButton;
import com.sqs.components.RCLabel;
import com.sqs.frames.LoginFrame;
import com.sqs.frames.MainFrame;
import com.sqs.frames.ShareCodeFrame;
import com.sqs.panel.treedemo.Profession;
import com.sqs.res.Colors;
import com.sqs.resourceshare.entity.ResourceChunk;
import com.sqs.resourceshare.entity.ResponseDto;
import com.sqs.util.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 文件上传界面
 */
public class UploadPanel extends JPanel {

    private RCLabel label;

    // 输入文件下载编码
    private JTextField num_tf;
    private JTextArea textArea;// 短文本、短地址
    private RCButton upload_button1;// 上传
    private RCButton uploadAndShare_button1;// 上传并共享

    private RCButton upload_button2;// 上传
    private RCButton uploadAndShare_button2;// 上传并共享

    private RCButton choose_button;
    // 切换上传的是公共文件还是私密文件
    private JComboBox<String> changeSource;
    private JPanel upPanel;
    private JTree fileTree = null;
    // 上传进度
    private JProgressBar jProgressBar;
    private JLabel currentFolder;//当前选中
    private LoginFrame loginFrame;
    DefaultMutableTreeNode root = null;
    DefaultTreeModel treeModel;//树的数据模型

    private String currentFolderId = null;

    public UploadPanel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        initComponents();
        initView();
    }

    private void initComponents() {
        root = new DefaultMutableTreeNode(
                new Profession("0", "公共文件", "folder"));
        treeModel = new DefaultTreeModel(root);//创建树的数据模型
        fileTree = new JTree(treeModel);
        changeSource = new JComboBox<String>();
        changeSource.addItem("公共文件");
        changeSource.addItem("私人文件");

        upPanel = new JPanel();
        label = new RCLabel("上传文本：");
        num_tf = new JTextField();
        num_tf.setEditable(false);
        choose_button = new RCButton("浏览", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);

        textArea = new JTextArea();

        upload_button1 = new RCButton("上传", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        uploadAndShare_button1 = new RCButton("上传并共享", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER,
                Colors.MAIN_COLOR_DARKER);

        upload_button2 = new RCButton("上传", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        uploadAndShare_button2 = new RCButton("上传并共享", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER,
                Colors.MAIN_COLOR_DARKER);
        jProgressBar = new JProgressBar(0, 100);
        currentFolder = new JLabel("上传目录");

    }

    private void initView() {

        // this.setPreferredSize(new Dimension(300, 400));
        upPanel.setLayout(null);
        label.setBounds(10, 40, 100, 30);

        upPanel.add(label);

        upPanel.add(textArea);
        textArea.setBounds(120, 40, 300, 100);
        textArea.setText("");

        upPanel.add(upload_button1);
        upload_button1.setBounds(430, 70, 80, 30);
        upPanel.add(uploadAndShare_button1);
        uploadAndShare_button1.setBounds(530, 70, 130, 30);

        int y = 180;

        JLabel line_label = new JLabel();
        line_label.setBorder(BorderFactory.createLineBorder(Colors.FONT_GRAY));
        line_label.setBounds(0, y - 30, 700, 2);
        upPanel.add(line_label);

        label = new RCLabel("文件路径：");
        label.setBounds(10, y, 100, 30);
        upPanel.add(label);

        num_tf.setPreferredSize(new Dimension(150, 30));
        upPanel.add(num_tf);
        num_tf.setBounds(120, y, 150, 30);

        choose_button.setBounds(280, y, 80, 30);
        upPanel.add(choose_button);

        upPanel.add(upload_button2);
        upload_button2.setBounds(430, y, 80, 30);
        upPanel.add(uploadAndShare_button2);
        uploadAndShare_button2.setBounds(530, y, 130, 30);

        jProgressBar.setBounds(0, 20, 700, 10);
        upPanel.add(jProgressBar);

        currentFolder.setBounds(0, 0, 700, 20);
        upPanel.add(currentFolder);


        this.setLayout(new BorderLayout());
        fileTree.setPreferredSize(new Dimension(300, 500));
        changeSource.setPreferredSize(new Dimension(300, 30));

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(changeSource, BorderLayout.WEST);

        add(jPanel, BorderLayout.NORTH);
        add(fileTree, BorderLayout.WEST);
        add(upPanel, BorderLayout.CENTER);

        choose_button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.setMultiSelectionEnabled(false);
                if (jFileChooser.showOpenDialog(MainFrame.getContext()) == JFileChooser.APPROVE_OPTION) {
                    // 选中文件
                    File selectFile = jFileChooser.getSelectedFile();
                    num_tf.setText(selectFile.getPath());
                    // 执行文件上传进程
                    // uploadFile(selectFile);

                }

            }
        });
        // 上传文本
        upload_button1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                uploadText(textArea.getText(), false);
            }

        });
        // 上传文本并共享
        uploadAndShare_button1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                uploadText(textArea.getText(), true);
            }

        });

        // 上传文件
        upload_button2.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                String text = num_tf.getText();
                if (text.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请选择上传文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                File file = new File(num_tf.getText());
                uploadFile(file, false);

            }

        });
        // 上传文件并共享
        uploadAndShare_button2.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                String text = num_tf.getText();
                if (text.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请选择上传文件", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                File file = new File(num_tf.getText());
                uploadFile(file, true);
               /* ShowShareCodeFrame showShareCodeFrame = new ShowShareCodeFrame();
                showShareCodeFrame.setVisible(true);*/
            }

        });

        changeSource.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentFolder.setText("上传目录:");
                currentFolderId = null;
                if (changeSource.getSelectedItem().toString().equals("私人文件")) {
                    // 判断用户是否登录
                    boolean result = LoginFrame.validateLogin();
                    // 登录成功
                    if (result) {
                        // 逻辑待添加
                        root.setUserObject(new Profession("0", "私人文件", "folder"));
                        initData(root);
                    } else {
                        changeSource.setSelectedIndex(0);
                    }
                } else {
                    root.setUserObject(new Profession("0", "公共文件", "folder"));
                    initData(root);
                }
            }
        });
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
                    if (selectedNode != null) {
                        Profession userObject = (Profession) (selectedNode).getUserObject();
                        if (userObject.getType().equals("folder")) {
                            System.out.println("folder");
                            currentFolder.setText("上传目录:" + userObject.getName());
                            currentFolderId = userObject.getCode();
                            initData(selectedNode);
                        } else {
                            JOptionPane.showMessageDialog(null, "只能上传至文件夹", "提示", JOptionPane.WARNING_MESSAGE);
                        }

                    }
                }
            }
        });
        fileTree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                Profession userObject = (Profession) ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject.getType().equals("folder")) {
                    Icon icon = IconUtil.getIcon(this, "/fileico/folder.png", 20, 20, true);
                    setIcon(icon);
                } else {
                    Icon icon = IconUtil.getIcon(this, "/fileico/file.png", 20, 20, true);
                    setIcon(icon);
                }
                setText(userObject.getName());
                return this;
            }
        });
    }

    public void setUploadFilePath(String path){
        num_tf.setText(path);
    }

    //更新界面数据
    public void initData(DefaultMutableTreeNode currentTreeNode) {
        if (currentTreeNode == null) {
            currentTreeNode = root;
        }
        currentTreeNode.removeAllChildren();
        Profession userObject = (Profession) currentTreeNode.getUserObject();
        DefaultMutableTreeNode finalCurrentTreeNode = currentTreeNode;
        String url = "";
        String owner = null;
        if (changeSource.getSelectedItem().toString().equals("私人文件")) {
            root.setUserObject(new Profession("0", "私人文件", "folder"));
            owner = LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName();
        } else {
            root.setUserObject(new Profession("0", "公共文件", "folder"));
        }


        if (owner == null) {
            url = Launcher.getServerUrl() + "/file/parent/" + userObject.getCode();
        } else {
            url = Launcher.getServerUrl() + "/personfile/parent/" + userObject.getCode() + "/" + owner;
        }

        HttpClientUtil.doGet(url, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                //tableModel.setRowCount(0);
                ResponseDto<java.util.List<com.sqs.entity.File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        List<LinkedTreeMap<String, Object>> files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            String type = file.get("type").toString();
                            String fileName = file.get("fileName").toString();
                            String fileId = String.valueOf(Double.valueOf(file.get("id").toString()).longValue());
                            Profession userObject = new Profession(fileId,
                                    fileName, type);
                            DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);
                            node.setAllowsChildren(true);
							/*if(type.equals("folder")){
								node.add(new DefaultMutableTreeNode("test"));
							}*/
                            //node.I
                            finalCurrentTreeNode.add(node);
                        }
                        treeModel.reload(finalCurrentTreeNode);
                        TreePath treePath = new TreePath(treeModel.getPathToRoot(finalCurrentTreeNode));

                        // 展开节点
                        fileTree.makeVisible(treePath);
                        fileTree.scrollPathToVisible(treePath);
                    }
                }
            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });


    }

    // 上传文本
    private void uploadText(String context, boolean share) {
        String text = textArea.getText();
        if (text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "文本不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentFolderId == null) {
            JOptionPane.showMessageDialog(null, "请选择上传文件夹", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fileName = StringUtils.getFileName(context);
        // 文本写文件
        String writeFilePath = Launcher.appFilesBasePath + File.separator + fileName;
        File tempFile = new File(writeFilePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(context.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 文本上传
        uploadOneFile(tempFile, share);
    }

    //针对文本上传
    private void uploadOneFile(File file, boolean share) {
        ResourceChunk resourceChunk = new ResourceChunk();
        resourceChunk.setChunkNumber(1);
        resourceChunk.setChunkSize(Md5Util.CHUNKSIZE);
        resourceChunk.setCurrentChunkSize(file.length());
        resourceChunk.setTotalChunks(1);
        resourceChunk.setTotalSize(file.length());
        resourceChunk.setIdentifier(GenerateUniqueIdentifier.generateUniqueIdentifier(file));
        resourceChunk.setRelativePath(file.getName());
        resourceChunk.setFilename(file.getName());
        jProgressBar.setMaximum(100);
        jProgressBar.setMinimum(1);
        jProgressBar.setValue(30);
        // resourceChunk.setFile(file);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Launcher.getServerUrl() + "/resource/chunk";
                HttpClientUtil.uploadFile(url, resourceChunk, file, new HttpCallBack() {
                    @Override
                    public void response(String result) {
                        jProgressBar.setValue(50);
                        /*//文件上传成功
                        if ("200".equals(result)) {
                        }*/
                        String mergeUrl = Launcher.getServerUrl() + "/resource/mergejson";
                        com.sqs.entity.File fileEntity = new com.sqs.entity.File();
                        fileEntity.setParentId(Long.valueOf(currentFolderId));
                        fileEntity.setFileName(resourceChunk.getFilename());
                        if (changeSource.getSelectedItem().toString().equals("私人文件")) {
                            fileEntity.setOwner(LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName());
                        }
                        fileEntity.setIdentifier(resourceChunk.getIdentifier());
                        fileEntity.setType("txt");
                        fileEntity.setSize(resourceChunk.getTotalSize());
                        fileEntity.setShareCode(String.valueOf(share));

                        //合并文件块
                        HttpClientUtil.doPostJson(mergeUrl, new Gson().toJson(fileEntity), new HttpCallBack() {
                            @Override
                            public void response(String result) {
                                jProgressBar.setValue(100);
                                System.out.println(result);
                                num_tf.setText("");
                                textArea.setText("");
                                if (share && result != null) {
                                    ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);
                                    ShareCodeFrame shareCodeFrame = new ShareCodeFrame(temp.getData().toString());
                                    shareCodeFrame.setVisible(true);
                                    initData(root);
                                } else {
                                    initData(root);
                                    JOptionPane.showMessageDialog(null, "文本上传成功,文件名：" + file.getName(), "提示", JOptionPane.WARNING_MESSAGE);
                                }
                                //开始共享
                            }

                            @Override
                            public void error(String error) {
                                JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                            }
                        });
                    }

                    @Override
                    public void error(String error) {
                        //文件上传失败
                        JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                    }
                });
            }
        }).start();

    }

    // 上传文件
    private void uploadFile(File file, boolean share) {
        jProgressBar.setMaximum(100);
        jProgressBar.setMinimum(1);
        jProgressBar.setValue(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Long totalSize = file.length();
                Long chunkSize = Md5Util.CHUNKSIZE;
                Integer totalChunks = (int) (totalSize % chunkSize == 0 ? totalSize / chunkSize : totalSize / chunkSize + 1);
                int step = 100 / totalChunks;
                for (int i = 0; i < totalChunks; i++) {
                    jProgressBar.setValue(step * (i + 1));
                    long currentSize = chunkSize;
                    //生成文件块
                    ResourceChunk resourceChunk = new ResourceChunk();
                    resourceChunk.setChunkNumber(i + 1);
                    resourceChunk.setChunkSize(chunkSize);
                    if (i < totalChunks - 1) {
                        resourceChunk.setCurrentChunkSize(currentSize);
                    } else {
                        currentSize = totalSize - (i * chunkSize);
                        resourceChunk.setCurrentChunkSize(currentSize);
                    }
                    resourceChunk.setTotalChunks(totalChunks);
                    resourceChunk.setTotalSize(file.length());
                    String fileName = file.getName();
                    resourceChunk.setIdentifier(GenerateUniqueIdentifier.generateUniqueIdentifier(totalSize, fileName));
                    resourceChunk.setRelativePath(fileName);
                    resourceChunk.setFilename(fileName);


                    String url = Launcher.getServerUrl() + "/resource/chunk";
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        fileInputStream.skip(i * chunkSize);
                        byte buf[] = new byte[(int) currentSize];
                        fileInputStream.read(buf);
                        fileInputStream.close();
                        int finalI = i;

                        Map<String, String> params = new HashMap<>();
                        params.put("identifier", resourceChunk.getIdentifier());
                        params.put("chunkNumber", resourceChunk.getChunkNumber().toString());
                        //验证文件块是否上传
                        HttpClientUtil.doGet(Launcher.getServerUrl() + "/resource/chunk", params, new HttpCallBack() {
                            @Override
                            public void response(String result) {
                                ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);
                                System.out.println(result);
                                if (temp!=null&&"该文件块已经上传".equals(temp.getData())) {
                                    //最后块上传
                                    if (finalI == (totalChunks - 1)) {
                                        mergeResourceFromJson(resourceChunk, share, file);
                                    }
                                } else {
                                    HttpClientUtil.uploadFile(url, resourceChunk, buf, new HttpCallBack() {
                                        @Override
                                        public void response(String result) {
                                            System.out.println(result);
                                            //最后块上传
                                            if (finalI == (totalChunks - 1)) {
                                                mergeResourceFromJson(resourceChunk, share, file);
                                            }
                                        }

                                        @Override
                                        public void error(String error) {
                                            //文件上传失败
                                            JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void error(String error) {

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //if()
            }
        }).start();
    }

    /***
     * 合并存储文件逻辑记录
     */
    private void mergeResourceFromJson(ResourceChunk resourceChunk, boolean share, File file) {
        String mergeUrl = Launcher.getServerUrl() + "/resource/mergejson";
        com.sqs.entity.File fileEntity = new com.sqs.entity.File();
        fileEntity.setParentId(Long.valueOf(currentFolderId));
        fileEntity.setFileName(resourceChunk.getFilename());
        if (changeSource.getSelectedItem().toString().equals("私人文件")) {
            fileEntity.setOwner(LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName());
        }
        //fileEntity.setOwner(LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName());
        fileEntity.setIdentifier(resourceChunk.getIdentifier());
        fileEntity.setType(StringUtils.getFileType(resourceChunk.getFilename()));
        fileEntity.setSize(resourceChunk.getTotalSize());
        fileEntity.setShareCode(String.valueOf(share));


        //合并文件块
        HttpClientUtil.doPostJson(mergeUrl, new Gson().toJson(fileEntity), new HttpCallBack() {
            @Override
            public void response(String result) {
                jProgressBar.setValue(100);
                System.out.println(result);
                num_tf.setText("");
                textArea.setText("");
                if (share && result != null) {
                    ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);
                    ShareCodeFrame shareCodeFrame = new ShareCodeFrame(temp.getData().toString());
                    shareCodeFrame.setVisible(true);
                } else {
                    initData(root);
                    JOptionPane.showMessageDialog(null, "文件上传成功,文件名：" + file.getName(), "提示", JOptionPane.WARNING_MESSAGE);
                }
                //开始共享
            }

            @Override
            public void error(String error) {
                JOptionPane.showMessageDialog(null, "文件上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

    }


}
