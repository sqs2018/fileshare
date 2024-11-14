package com.sqs.resourceshare_android.ui.fileupload;


import static com.blankj.molihuan.utilcode.util.ViewUtils.runOnUiThread;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bin.david.form.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.molihuan.pathselector.PathSelector;
import com.molihuan.pathselector.entity.FileBean;
import com.molihuan.pathselector.entity.FontBean;
import com.molihuan.pathselector.fragment.BasePathSelectFragment;
import com.molihuan.pathselector.fragment.impl.PathSelectFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.listener.FileItemListener;
import com.molihuan.pathselector.utils.MConstants;
import com.molihuan.pathselector.utils.Mtools;
import com.sqs.resourceshare_android.ShowShareCodeActivity;
import com.sqs.resourceshare_android.databinding.FragmentFileuploadBinding;
import com.sqs.resourceshare_android.entity.File;
import com.sqs.resourceshare_android.entity.ResourceChunk;
import com.sqs.resourceshare_android.entity.ResponseDto;
import com.sqs.resourceshare_android.ui.login.LoginActivity;
import com.sqs.resourceshare_android.util.Data;
import com.sqs.resourceshare_android.util.FileUtils;
import com.sqs.resourceshare_android.util.GenerateUniqueIdentifier;
import com.sqs.resourceshare_android.util.HttpCallBack;
import com.sqs.resourceshare_android.util.HttpClientUtil;
import com.sqs.resourceshare_android.util.StringUtils;
import com.sqs.resourceshare_android.util.tree.TreeViewAdapt;
import com.sqs.resourceshare_android.util.tree.TreeViewSetting;
import com.sqs.resourceshare_android.util.tree.model.TreeNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class FileuploadFragment extends Fragment {

    private FragmentFileuploadBinding binding;
    TreeNode root;
    private TreeNode currentNode = null;
    private Long currentClickTime = 0L;
    static PathSelectFragment selector;
    //选择文件路径
    String selectFile;

    ProgressBar progressBar;
    AlertDialog dialog;


    private final static int APP_STORAGE_ACCESS_REQUEST_CODE = 501;
    private static final int REQUEST_STORAGE_PERMISSIONS = 123;
    private static final int REQUEST_MEDIA_PERMISSIONS = 456;
    private final String readPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String writePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    binding.tree.setTreeData(root, true);
                    break;
                case 2:
                    dialog.hide();
                default:
                    break;
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFileuploadBinding.inflate(inflater, container, false);
        View rootTemp = binding.getRoot();

        String[] items = new String[]{"公共文件", "私人文件"};
        // SpinnerAdapter apsAdapter= binding.spinnerSimple.getAdapter();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSimple.setAdapter(adapter);

        root = new TreeNode();
        root.setData("公共文件", 0L, "folder");

        binding.tree.setTreeData(root, true);


        progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true); // 设置为不确定模式
// 创建AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(progressBar); // 设置ProgressBar为对话框的视图
        builder.setCancelable(false); // 设置对话框不可取消

// 创建并显示对话框
        dialog = builder.create();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    initData(root);
                });


        binding.spinnerSimple.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = binding.spinnerSimple.getItemAtPosition(i).toString();
                binding.uploadFolder.setText("上传目录:");
                currentNode = null;
                if (text.equals("公共文件")) {
                    initData(root);
                } else {
                    if (Data.loginUser == null) {
                        Toast.makeText(getActivity(), "请登录系统", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        activityResultLauncher.launch(intent);
                        return;
                    }
                    initData(root);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.tree.getTreeViewAdapt().setOnItemClickListener(new TreeViewAdapt.OnItemClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position, TreeNode node) {
                // binding.tree.getItemAtPosition(position);
                if (System.currentTimeMillis() - currentClickTime < 500) {
                    if ("folder".equals(node.getFileType())) {
                        currentNode = (TreeNode) binding.tree.getItemAtPosition(position);
                        binding.uploadFolder.setText("上传目录：" + currentNode.getName());
                        initData(currentNode);
                    } else {
                        Toast.makeText(getActivity(), "请选择文件夹上传", Toast.LENGTH_LONG).show();
                    }

                } else {
                    currentClickTime = System.currentTimeMillis();
                }

                // Toast.makeText(getActivity(), "请登录系统", Toast.LENGTH_LONG).show();
            }
        });

        //文本上传
        binding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.editText.getText().toString();
                uploadText(text, false);
            }
        });

        //文本上传并共享
        binding.uploadAndShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.editText.getText().toString();
                uploadText(text, true);
            }
        });
        binding.chooseFileButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // intent.setType("*/*");
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                //fileActivityResultLauncher.launch(intent);

                selector = PathSelector.build(getActivity(), MConstants.BUILD_DIALOG)//Dialog构建方式
                        .setTitlebarMainTitle(new FontBean("选择文件"))
                        .setFileItemListener(
                                new FileItemListener() {
                                    @Override
                                    public boolean onClick(View v, FileBean file, String currentPath, BasePathSelectFragment pathSelectFragment) {
                                        // Mtools.toast("you clicked path:\n" + file.getPath());
                                        selectFile = file.getPath();
                                        binding.filePath.setText(selectFile);
                                        if (pathSelectFragment != null) {
                                            pathSelectFragment.close();
                                        }
                                        return true;
                                    }
                                }
                        )
                        /*.setMorePopupItemListeners(
                                new CommonItemListener("确定") {
                                    @Override
                                    public boolean onClick(View v, TextView tv, List<FileBean> selectedFiles, String currentPath, BasePathSelectFragment pathSelectFragment) {

                                        *//**取消dialog弹窗
                         * pathSelectFragment.close();
                         *//*

                                        StringBuilder builder = new StringBuilder();
                                        builder.append("you selected:\n");
                                        for (FileBean fileBean : selectedFiles) {
                                            builder.append(fileBean.getPath() + "\n");
                                        }
                                        Mtools.toast(builder.toString());

                                        return false;
                                    }
                                }
                        )*/
                        .show();//开始构建
            }
        });

// Toast.makeText(getActivity(), "请登录系统", Toast.LENGTH_LONG).show();
        //文件上传
        binding.uploadButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(selectFile, false);
            }
        });

        //文件上传并共享
        binding.uploadAndShareButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(selectFile, true);
            }
        });


        return rootTemp;
    }


    public void initData(TreeNode currentTreeNode) {
        if (currentTreeNode == null) {
            currentTreeNode = root;
        }
        String url = "";
        String owner = null;
        if (binding.spinnerSimple.getSelectedItem().toString().equals("私人文件")) {
            root.setData("私人文件", 0L, "folder");
            owner = Data.loginUser == null ? null : Data.loginUser.getUserName();
        } else {
            root.setData("公共文件", 0L, "folder");
        }
        //  root.removeNode();

        if (owner == null) {
            url = Data.SERVERURL + "/file/parent/" + currentTreeNode.getFileId();
        } else {
            url = Data.SERVERURL + "/personfile/parent/" + currentTreeNode.getFileId() + "/" + owner;
        }

        TreeNode finalCurrentTreeNode = currentTreeNode;
        HttpClientUtil.doGet(url, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                //tableModel.setRowCount(0);
                ResponseDto<java.util.List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        List<LinkedTreeMap<String, Object>> files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        finalCurrentTreeNode.removeNode();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            String type = file.get("type").toString();
                            String fileName = file.get("fileName").toString();
                            Long fileId = Double.valueOf(file.get("id").toString()).longValue();

                            TreeNode node = new TreeNode();
                            node.setData(fileName, fileId, type);
                            finalCurrentTreeNode.getNodes().add(node);
                        }
                        Message msg = Message.obtain(); // 实例化消息对象
                        msg.what = 1; // 消息标识
                        handler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void error(String error) {
                //JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("数据加载异常：" + error);
            }
        });

    }


    private void showToast(String message) {
        Looper.prepare(); // 初始化Looper
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                    }
                });

                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        };
        Message msg = Message.obtain(handler, 0);
        handler.sendMessage(msg); // 发送消息到消息队列
        Looper.loop(); // 启动消息循环
    }




    // 上传文本
    private void uploadText(String text, boolean share) {
        if (text.trim().isEmpty()) {
            //JOptionPane.showMessageDialog(null, "文本不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            Toast.makeText(getActivity(), "文本不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentNode == null) {
            //JOptionPane.showMessageDialog(null, "请选择上传文件夹", "提示", JOptionPane.WARNING_MESSAGE);
            Toast.makeText(getActivity(), "请选择上传文件夹", Toast.LENGTH_LONG).show();
            return;
        }

        String fileName = StringUtils.getFileName(text);
        // 文本写文件
        String writeFilePath = FileUtils.getExternalStoragePath() + java.io.File.separator + fileName;
        java.io.File tempFile = new java.io.File(writeFilePath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(tempFile);
            fileOutputStream.write(text.getBytes());
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
    private void uploadOneFile(java.io.File file, boolean share) {
        ResourceChunk resourceChunk = new ResourceChunk();
        resourceChunk.setChunkNumber(1);
        resourceChunk.setChunkSize(20480000L);
        resourceChunk.setCurrentChunkSize(file.length());
        resourceChunk.setTotalChunks(1);
        resourceChunk.setTotalSize(file.length());
        resourceChunk.setIdentifier(GenerateUniqueIdentifier.generateUniqueIdentifier(file));
        resourceChunk.setRelativePath(file.getName());
        resourceChunk.setFilename(file.getName());
        /*jProgressBar.setMaximum(100);
        jProgressBar.setMinimum(1);
        jProgressBar.setValue(30);*/
        // resourceChunk.setFile(file);
        String url = Data.SERVERURL + "/resource/chunk";
        HttpClientUtil.uploadFile(url, resourceChunk, file, new HttpCallBack() {
            @Override
            public void response(String result) {
                //jProgressBar.setValue(50);
                        /*//文件上传成功
                        if ("200".equals(result)) {
                        }*/
                String mergeUrl = Data.SERVERURL + "/resource/mergejson";
                com.sqs.resourceshare_android.entity.File fileEntity = new com.sqs.resourceshare_android.entity.File();
                fileEntity.setParentId(Long.valueOf(currentNode.getFileId()));
                fileEntity.setFileName(resourceChunk.getFilename());

                if (binding.spinnerSimple.getSelectedItem().toString().equals("私人文件")) {
                    fileEntity.setOwner(Data.loginUser == null ? null : Data.loginUser.getUserName());
                }
                fileEntity.setIdentifier(resourceChunk.getIdentifier());
                fileEntity.setType("txt");
                fileEntity.setSize(String.valueOf(resourceChunk.getTotalSize()));
                fileEntity.setShareCode(String.valueOf(share));

                //合并文件块
                HttpClientUtil.doPostJson(mergeUrl, new Gson().toJson(fileEntity), new HttpCallBack() {
                    @Override
                    public void response(String result) {
                        //jProgressBar.setValue(100);
                        System.out.println(result);
                        binding.editText.setText("");
                        //textArea.setText("");
                        binding.filePath.setText("");
                        if (share && result != null) {
                            ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);
                            //显示共享码
                            Intent intent = new Intent(getActivity(), ShowShareCodeActivity.class);
                            intent.putExtra("shareCode", temp.getData().toString());
                            startActivity(intent);
                                  /*  ShareCodeFrame shareCodeFrame = new ShareCodeFrame(temp.getData().toString());
                                    shareCodeFrame.setVisible(true);*/
                            initData(root);
                        } else {
                            initData(root);
                            //JOptionPane.showMessageDialog(null, "文本上传成功", "提示", JOptionPane.WARNING_MESSAGE);
                            showToast("文本上传成功,文件名：" + file.getName());
                        }
                        //开始共享
                    }

                    @Override
                    public void error(String error) {
                        //JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                        showToast("文本上传失败" + error);
                    }
                });
            }

            @Override
            public void error(String error) {
                //文件上传失败
                // JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("文本上传失败" + error);
            }
        });

    }


    // 上传文件
    private void uploadFile(String filePath, boolean share) {


        if (filePath == null) {
            //JOptionPane.showMessageDialog(null, "文本不能为空", "提示", JOptionPane.WARNING_MESSAGE);
            Toast.makeText(getActivity(), "文件不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentNode == null) {
            //JOptionPane.showMessageDialog(null, "请选择上传文件夹", "提示", JOptionPane.WARNING_MESSAGE);
            Toast.makeText(getActivity(), "请选择上传文件夹", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setMax(100);
        progressBar.setMin(0);
        progressBar.setProgress(0);
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                java.io.File file = new java.io.File(filePath);
       /* FileInputStream fileInputStream = null;
        Long totalSize = null;
        try {
            fileInputStream = new FileInputStream(file);
            totalSize =file.length();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Long chunkSize = 20480000L;
        Integer totalChunks = (int) (totalSize % chunkSize == 0 ? totalSize / chunkSize : totalSize / chunkSize + 1);
        // int step = 100 / totalChunks;
        for (int i = 0; i < totalChunks; i++) {
            //jProgressBar.setValue(step * (i + 1));
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
            resourceChunk.setTotalSize(totalSize);
            String fileName = file.getName();
            resourceChunk.setIdentifier(GenerateUniqueIdentifier.generateUniqueIdentifier(totalSize, fileName));
            resourceChunk.setRelativePath(fileName);
            resourceChunk.setFilename(fileName);





            String url = Data.SERVERURL + "/resource/chunk";
            try {
                fileInputStream.skip(i * chunkSize);
                byte buf[] = new byte[(int) currentSize];
                fileInputStream.read(buf);
                fileInputStream.close();
                int finalI = i;
                HttpClientUtil.uploadFile(url, resourceChunk, buf, new HttpCallBack() {
                    @Override
                    public void response(String result) {
                        System.out.println(result);
                        if (finalI == (totalChunks - 1)) {
                            String mergeUrl = Data.SERVERURL + "/resource/mergejson";
                            com.sqs.resourceshare_android.entity.File fileEntity = new com.sqs.resourceshare_android.entity.File();
                            fileEntity.setParentId(Long.valueOf(currentNode.getFileId()));
                            fileEntity.setFileName(resourceChunk.getFilename());
                            if (binding.spinnerSimple.getSelectedItem().toString().equals("私人文件")) {
                                fileEntity.setOwner(Data.loginUser == null ? null : Data.loginUser.getUserName());
                            }
                            //fileEntity.setOwner(LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName());
                            fileEntity.setIdentifier(resourceChunk.getIdentifier());
                            fileEntity.setType(StringUtils.getFileType(resourceChunk.getFilename()));
                            fileEntity.setSize(String.valueOf(resourceChunk.getTotalSize()));
                            fileEntity.setShareCode(String.valueOf(share));


                            //合并文件块
                            HttpClientUtil.doPostJson(mergeUrl, new Gson().toJson(fileEntity), new HttpCallBack() {
                                @Override
                                public void response(String result) {
                                    // jProgressBar.setValue(100);
                                    System.out.println(result);
                                    //num_tf.setText("");
                                    //textArea.setText("");
                                    binding.editText.setText("");
                                    if (share && result != null) {
                                        ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);

                                        Intent intent = new Intent(getActivity(), ShowShareCodeActivity.class);
                                        intent.putExtra("shareCode", temp.getData().toString());
                                        startActivity(intent);
                                    } else {
                                        initData(root);
                                        // JOptionPane.showMessageDialog(null, "文件上传成功", "提示", JOptionPane.WARNING_MESSAGE);
                                        showToast("文件上传成功");
                                    }
                                    //开始共享
                                }

                                @Override
                                public void error(String error) {
                                    // JOptionPane.showMessageDialog(null, "文件上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                                    //Toast.makeText(getActivity(), "文件上传失败" + error, Toast.LENGTH_LONG).show();
                                    showToast("文件上传失败" + error);
                                }
                            });

                        }
                    }

                    @Override
                    public void error(String error) {
                        //文件上传失败
                        //JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                        //Toast.makeText(getActivity(), "文件上传失败" + error, Toast.LENGTH_LONG).show();
                        showToast("文件上传失败" + error);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/


                Long totalSize = file.length();
                Long chunkSize = 20480000L;
                Integer totalChunks = (int) (totalSize % chunkSize == 0 ? totalSize / chunkSize : totalSize / chunkSize + 1);
                int step = 100 / totalChunks;
                for (int i = 0; i < totalChunks; i++) {
                    progressBar.setProgress(step * (i + 1));
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

                    String url = Data.SERVERURL + "/resource/chunk";
                    try {

                        int finalI = i;

                        Map<String, String> params = new HashMap<>();
                        params.put("identifier", resourceChunk.getIdentifier());
                        params.put("chunkNumber", resourceChunk.getChunkNumber().toString());
                        //验证文件块是否上传
                        long finalCurrentSize = currentSize;
                        HttpClientUtil.doGet(Data.SERVERURL + "/resource/chunk", params, new HttpCallBack() {
                            @Override
                            public void response(String result) {
                                ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);
                                System.out.println(result);
                                if (temp != null && "该文件块已经上传".equals(temp.getData())) {
                                    //最后块上传
                                    if (finalI == (totalChunks - 1)) {
                                        mergeResourceFromJson(resourceChunk, share, file);
                                    }
                                } else {
                                    ByteArrayOutputStream byteArrayOutputStream = null;
                                    try {
                                        FileInputStream fileInputStream = new FileInputStream(file);
                                        fileInputStream.skip(finalI * chunkSize);

                                        byteArrayOutputStream = new ByteArrayOutputStream();
                                        for (int j = 0; j < 10; j++) {
                                            byte buf[] = new byte[(int) finalCurrentSize / 10];
                                            fileInputStream.read(buf);
                                            byteArrayOutputStream.write(buf);
                                        }
                                        fileInputStream.close();
                                    } catch (Exception e) {

                                    }

                                    HttpClientUtil.uploadFile(url, resourceChunk, byteArrayOutputStream.toByteArray(), new HttpCallBack() {
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
                                            // JOptionPane.showMessageDialog(null, "文本上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                                            showToast("文件上传失败" + error);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void error(String error) {
                                showToast("文件上传失败" + error);
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        //if()
    }

    private void mergeResourceFromJson(ResourceChunk resourceChunk, boolean share, java.io.File file) {

        String mergeUrl = Data.SERVERURL + "/resource/mergejson";
        File fileEntity = new File();
        fileEntity.setParentId(Long.valueOf(currentNode.getFileId()));
        fileEntity.setFileName(resourceChunk.getFilename());
        if (binding.spinnerSimple.getSelectedItem().toString().equals("私人文件")) {
            fileEntity.setOwner(Data.loginUser == null ? null : Data.loginUser.getUserName());
        }
        //fileEntity.setOwner(LoginFrame.Loginuser == null ? null : LoginFrame.Loginuser.getUserName());
        fileEntity.setIdentifier(resourceChunk.getIdentifier());
        fileEntity.setType(StringUtils.getFileType(resourceChunk.getFilename()));
        fileEntity.setSize(resourceChunk.getTotalSize().toString());
        fileEntity.setShareCode(String.valueOf(share));
        progressBar.setProgress(100);

        //合并文件块
        HttpClientUtil.doPostJson(mergeUrl, new Gson().toJson(fileEntity), new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println(result);
                /*num_tf.setText("");
                textArea.setText("");*/
                binding.editText.setText("");
                //textArea.setText("");
                binding.filePath.setText("");
                if (share && result != null) {
                    showToast("文件上传成功");
                    ResponseDto temp = new Gson().fromJson(result, ResponseDto.class);
                    Intent intent = new Intent(getActivity(), ShowShareCodeActivity.class);
                    intent.putExtra("shareCode", temp.getData().toString());
                    startActivity(intent);
                } else {
                    initData(root);
                    //JOptionPane.showMessageDialog(null, "文件上传成功,文件名：" + file.getName(), "提示", JOptionPane.WARNING_MESSAGE);
                    showToast("文件上传成功");
                }
                //开始共享
            }

            @Override
            public void error(String error) {
                // JOptionPane.showMessageDialog(null, "文件上传失败" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("文件上传失败" + error);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}