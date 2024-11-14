package com.sqs.resourceshare_android.ui.publiclist;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.fonts.FontStyle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.draw.ImageResDrawFormat;
import com.bin.david.form.data.format.draw.TextImageDrawFormat;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bin.david.form.utils.DensityUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sqs.resourceshare_android.R;
import com.sqs.resourceshare_android.ShowShareCodeActivity;
import com.sqs.resourceshare_android.databinding.FragmentPubliclistBinding;
import com.sqs.resourceshare_android.entity.DownloadLog;
import com.sqs.resourceshare_android.entity.File;
import com.sqs.resourceshare_android.entity.ResponseDto;
import com.sqs.resourceshare_android.util.Data;
import com.sqs.resourceshare_android.util.FileUtils;
import com.sqs.resourceshare_android.util.HttpCallBack;
import com.sqs.resourceshare_android.util.HttpClientUtil;
import com.sqs.resourceshare_android.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class PublicFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 100;
    private FragmentPubliclistBinding binding;

    private SmartTable<File> table;
    private List<File> codeList = new ArrayList<>();
    Column<String> fileName;
    Column<Integer> sizeColumn;
    Column<String> updateTime;
    Column<String> shareCode;
    Column<Boolean> operation;
    TableData<File> tableData;
    private int currentFileId;//当前目录Id
    private Stack<Long> pathStack = new Stack<>();
    long clickThisTime = 0L;

    AlertDialog dialog;
    List<String> name_selected = new ArrayList<String>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPubliclistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        table = binding.table;
        int size = DensityUtils.dp2px(getContext(), 30);

        fileName = new Column<>("文件名", "fileName", new TextImageDrawFormat<String>(size, size, 5) {
            @Override
            protected Context getContext() {
                return PublicFragment.this.getContext();
            }

            @Override
            protected int getResourceID(String file, String value, int position) {
                if (codeList != null) {
                    for (File f : codeList) {
                        if (f.getFileName().equals(file) && f.getType().equals("folder")) {
                            return R.drawable.folder;
                        }
                    }
                }

                return R.drawable.file;
            }
        });
        fileName.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String value, String s, int position) {
                //Toast.makeText(getActivity(), "点击了" + value, Toast.LENGTH_SHORT).show();
                //实现双击效果
                if (System.currentTimeMillis() - clickThisTime > 500) {
                    clickThisTime = System.currentTimeMillis();
                    return;
                }
                if (codeList != null) {
                    File f = codeList.get(position);
                    if ("folder".equals(f.getType())) {
                        pathStack.push(f.getParentId());
                        String path = binding.currentPathLabel.getText().toString();
                        if (path.endsWith("/")) {
                            binding.currentPathLabel.setText(binding.currentPathLabel.getText() + value);
                        } else {
                            binding.currentPathLabel.setText(binding.currentPathLabel.getText() + "/" + value);
                        }
                        initData(f.getId().intValue());
                    }

                }

            }
        });

        sizeColumn = new Column<>("大小", "size");



        shareCode = new Column<>("共享码", "shareCode");

        updateTime = new Column<>("更新时间", "updateTime");
        /*age.setOnColumnItemClickListener(new OnColumnItemClickListener<Integer>() {
            @Override
            public void onClick(Column<Integer> column, String value, Integer bool, int position) {
//                Toast.makeText(CodeListActivity.this,"点击了"+value,Toast.LENGTH_SHORT).show();
                if(operation.getDatas().get(position)){
                    showName(position, false);
                    operation.getDatas().set(position,false);
                }else{
                    showName(position, true);
                    operation.getDatas().set(position,true);
                }
               table.refreshDrawableState();
                table.invalidate();
            }
        });*/


        operation = new Column<>("勾选", "operation", new ImageResDrawFormat<Boolean>(size, size) {    //设置"操作"这一列以图标显示 true、false 的状态
            @Override
            protected Context getContext() {
                return PublicFragment.this.getContext();
            }

            @Override
            protected int getResourceID(Boolean isCheck, String value, int position) {
                if (isCheck) {
                    return R.drawable.checkbox_checked;      //将图标提前放入 app/res/mipmap 目录下
                }
                return R.drawable.unselect_check;
            }
        });
        operation.setComputeWidth(40);
        operation.setOnColumnItemClickListener(new OnColumnItemClickListener<Boolean>() {
            @Override
            public void onClick(Column<Boolean> column, String value, Boolean bool, int position) {
//                Toast.makeText(CodeListActivity.this,"点击了"+value,Toast.LENGTH_SHORT).show();
                if (operation.getDatas().get(position)) {
                    showName(position, false);
                    operation.getDatas().set(position, false);
                } else {
                    //清除所有选择
                    for (int i = 0; i < codeList.size(); i++) {
                        showName(i, false);
                        operation.getDatas().set(i, false);
                    }
                    showName(position, true);
                    operation.getDatas().set(position, true);
                }
                table.refreshDrawableState();
                table.invalidate();
            }
        });


        tableData = new TableData<>("file", codeList, operation, fileName, sizeColumn,shareCode, updateTime);
        table.getConfig().setShowTableTitle(false);
        table.setTableData(tableData);

        table.getConfig().setMinTableWidth(1024);       //设置表格最小宽度

        table.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
                if (cellInfo.row % 2 == 1) {
                    return ContextCompat.getColor(getContext(), R.color.white_1);      //需要在 app/res/values 中添加 <color name="tableBackground">#d4d4d4</color>
                } else {
                    return ContextCompat.getColor(getContext(), R.color.white);
                }
            }
        });
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = binding.fileNameEt.getText().toString();
                search(fileName);
            }
        });

        binding.preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pathStack.size() > 0) {
                    int fileId = pathStack.pop().intValue();
                    String path = binding.currentPathLabel.getText().toString();
                    int index = path.lastIndexOf("/");
                    if (index != -1) {
                        String subPath = path.substring(0, index);
                        if (subPath.indexOf("/") == -1) {
                            subPath += "/";
                        }
                        binding.currentPathLabel.setText(subPath);
                    }
                    initData(fileId);
                } else {
                    initData(0);
                }
            }
        });

        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getSelectFile();
                if (file != null) {
                    Toast.makeText(getContext(), "正在下载...", Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloadFile(file);
                        }
                    }).start();

                } else {
                    //showToast("请选择下载文件");
                    Toast.makeText(getContext(), "请选择下载文件", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getSelectFile();
                if (file != null) {
                    del(file.getId().toString());
                } else {
                    // showToast("请选择删除文件");
                    Toast.makeText(getContext(), "请选择删除文件", Toast.LENGTH_LONG).show();
                }
            }
        });


        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = getSelectFile();
                if (file != null) {
                    shareFile(file.getId().toString());
                } else {
                    //showToast("请选择共享文件");
                    Toast.makeText(getContext(), "请选择共享文件", Toast.LENGTH_LONG).show();
                }
                //shareFile();
            }
        });
        binding.createFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createFolder()

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(R.layout.dialog_input);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogTemp, int id) {
                        // 处理确定按钮点击事件
                        EditText editText = dialog.findViewById(R.id.editText);
                        String userInput = editText.getText().toString();
                        createFolder(userInput);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 处理取消按钮点击事件
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
        binding.showdownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getSelectFile();
                if (file != null) {
                    HttpClientUtil.doGet(Data.SERVERURL + "/downloadLog/"+file.getId(),null, new HttpCallBack() {
                        @Override
                        public void response(String result) {
                            System.out.println("=====>" + result);
                            if (result != null) {
                                ResponseDto<java.util.List<DownloadLog>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                                if (responseDto != null) {
                                    List<LinkedTreeMap<String, Object>> files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                                    if(files.size()>0){
                                        CharSequence[] items = new CharSequence[files.size()];
                                        for (int i = 0; i < files.size(); i++) {
                                            LinkedTreeMap<String, Object> file = files.get(i);
                                            Object userNameT=file.get("userName");
                                            items[i]=(i+1)+"."+((userNameT==null||userNameT.equals(""))?"匿名用户":userNameT.toString())+"\t"+StringUtils.formatDate(file.get("createTime").toString());
                                        }

                                        Looper.prepare(); // 初始化Looper
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());//will be crash: android.content.res.Resources$NotFoundException: Resource ID #0x0
                                        mBuilder = new AlertDialog.Builder(getContext());
                                        //填充列表数据
                                        mBuilder.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        mBuilder.setTitle("下载用户列表"); // 设置标题
                                        dialog = mBuilder.create();
                                        dialog.show();
                                        Looper.loop(); // 启动消息循环
                                    }else{
                                        showToast("无下载记录");
                                    }


                                }


                            }

                        }

                        @Override
                        public void error(String error) {
                            //JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                            showToast("数据加载异常：" + error);
                        }
                    });
                } else {
                    // showToast("请选择删除文件");
                    Toast.makeText(getContext(), "请选择文件", Toast.LENGTH_LONG).show();
                }



            }
        });


        initData(currentFileId);
        // final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void createFolder(String folder) {
        File file = new File();
        file.setFileName(folder);
        file.setParentId(Long.valueOf(currentFileId));
        file.setType("folder");
        //add-09-25
        HttpClientUtil.doPostJson(Data.SERVERURL + "/file/createFolder", new Gson().toJson(file), new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (result != null && "success".equals(responseDto.getMsg())) {
                    initData(currentFileId);
                } else {
                    // JOptionPane.showMessageDialog(null, responseDto.getMsg(), "提示", JOptionPane.WARNING_MESSAGE);
                    showToast(responseDto.getMsg());
                }

            }

            @Override
            public void error(String error) {
                //JOptionPane.showMessageDialog(null, "数据加载异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("数据加载异常：" + error);
            }
        });


    }


    private File getSelectFile() {
        for (int i = 0; i < operation.getDatas().size(); i++) {
            if (operation.getDatas().get(i)) {
                return codeList.get(i);
            }
        }
        return null;
    }

    private void search(String query) {
        if (query.isEmpty()) {
            initData(0);
            return;
        }
        HttpClientUtil.doGet(Data.SERVERURL + "/file/query/" + currentFileId + "/" + query, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        List<LinkedTreeMap<String, Object>> files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        codeList.clear();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            String type = file.get("type").toString();
                            File f = new File();
                            f.setType(type);
                            String shareCode = file.get("shareCode") == null ? "" : file.get("shareCode").toString();
                            f.setShareCode(shareCode);
                            if ("folder".equals(f.getType())) {
                                f.setSize("-");
                            } else {
                                f.setSize(file.get("size") == null ? "0" : file.get("size").toString());
                                f.setSize(f.getSizeStr());
                            }
                            f.setParentId(Double.valueOf(file.get("parentId").toString()).longValue());
                            f.setId(Double.valueOf(file.get("id").toString()).longValue());
                            f.setFileName(file.get("fileName").toString());
                            f.setUpdateTime(StringUtils.formatDate(file.get("updateTime").toString()));
                            codeList.add(f);
                          /*  if (type.equals("folder")) {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), "-", file.get("updateTime"),shareCode});
                                //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
                            } else {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), file.get("size"), file.get("updateTime"),shareCode});
                            }*/
                        }
                        tableData = new TableData<>("file", codeList, operation, fileName, sizeColumn, shareCode,updateTime);
                        table.getConfig().setShowTableTitle(false);
                        table.setTableData(tableData);

                        table.refreshDrawableState();
                        table.invalidate();
                    }

                }


            }

            @Override
            public void error(String error) {
                showToast("数据加载异常：" + error);
            }
        });


    }

    private void downloadFile(File file) {
//下载文件
        String fileUrl = null;
        if (file.getOwner() != null) {
            fileUrl = Data.SERVERURL + "/personfile/" + file.getId() + "/download/" + file.getOwner();
        } else {
            fileUrl = Data.SERVERURL + "/file/" + file.getId() + "/download";
        }
        String path = FileUtils.getExternalStoragePath() + java.io.File.separator + file.getFileName();
        if (file.getType().equals("folder")) {
            path += ".zip";
        }
        Log.d("DownloadFragment", "文件路径:" + path);
        String finalPath = path;
        HttpClientUtil.downloadFile(fileUrl, path, new HttpCallBack() {
            @Override
            public void response(String result) {
                showToast("文件路径：" + finalPath);
            }

            @Override
            public void error(String error) {
                showToast("下载文件失败：" + error);
            }
        });
    }


    /***
     * 删除
     * @param parentId
     */
    public void del(String parentId) {
        //v1/file/parent/0
        HttpClientUtil.doGet(Data.SERVERURL + "/file/del/" + parentId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        //JOptionPane.showMessageDialog(null, "删除成功", "提示", JOptionPane.WARNING_MESSAGE);
                        search(binding.fileNameEt.getText().toString());
                        showToast("删除成功");
                        //initData(currentFileId);
                        //重新查询

                    }

                }


            }

            @Override
            public void error(String error) {
                // JOptionPane.showMessageDialog(null, "异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("异常：" + error);
            }
        });


    }

    /***
     * 共享
     */
    public void shareFile(String fileId) {
        HttpClientUtil.doGet(Data.SERVERURL + "/file/share/" + fileId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        // JOptionPane.showMessageDialog(null, "共享成功", "提示", JOptionPane.WARNING_MESSAGE);
                        //initData(currentFileId);
                        //重新查询
                       /* ShareCodeFrame shareCodeFrame=new ShareCodeFrame(responseDto.getData().toString());
                        shareCodeFrame.setVisible(true);*/
                        initData(currentFileId);
                        Intent intent = new Intent(getActivity(), ShowShareCodeActivity.class);
                        intent.putExtra("shareCode", responseDto.getData().toString());
                        startActivity(intent);
                    }

                }


            }

            @Override
            public void error(String error) {
                //JOptionPane.showMessageDialog(null, "异常：" + error, "提示", JOptionPane.WARNING_MESSAGE);
                showToast("异常：" + error);
            }
        });
    }


    /**
     * 收集所有被勾选的姓名记录到 name_selected 集合中，并实时更新
     *
     * @param position      被选择记录的行数，根据行数用来找到其他列中该行记录对应的信息
     * @param selectedState 当前的操作状态：选中 || 取消选中
     */
    public void showName(int position, boolean selectedState) {
        List<String> rotorIdList = fileName.getDatas();
        if (position > -1) {
            String rotorTemp = rotorIdList.get(position);
            if (selectedState && !name_selected.contains(rotorTemp)) {            //当前操作是选中，并且“所有选中的姓名的集合”中没有该记录，则添加进去
                name_selected.add(rotorTemp);
            } else if (!selectedState && name_selected.contains(rotorTemp)) {     // 当前操作是取消选中，并且“所有选中姓名的集合”总含有该记录，则删除该记录
                name_selected.remove(rotorTemp);
            }
        }
        for (String s : name_selected) {
            System.out.print(s + " -- ");
        }
    }

    private void initData(int parentId) {
        currentFileId = parentId;
        //v1/file/parent/0
        HttpClientUtil.doGet(Data.SERVERURL + "/file/parent/" + parentId, null, new HttpCallBack() {
            @Override
            public void response(String result) {
                System.out.println("=====>" + result);
                /*tableModel.setRowCount(0);
                files = null;*/
                ResponseDto<List<File>> responseDto = new Gson().fromJson(result, ResponseDto.class);
                if (responseDto != null) {
                    if (responseDto.getMsg().equals("success")) {
                        codeList.clear();
                        List<LinkedTreeMap<String, Object>> files = (List<LinkedTreeMap<String, Object>>) responseDto.getData();
                        for (int i = 0; i < files.size(); i++) {
                            LinkedTreeMap<String, Object> file = files.get(i);
                            String type = file.get("type").toString();
                            File f = new File();
                            f.setType(type);
                            String shareCode = file.get("shareCode") == null ? "" : file.get("shareCode").toString();
                            f.setShareCode(shareCode);
                            if ("folder".equals(f.getType())) {
                                f.setSize("-");
                            } else {
                                f.setSize(file.get("size") == null ? "0" : file.get("size").toString());
                                f.setSize(f.getSizeStr());
                            }
                            f.setParentId(Double.valueOf(file.get("parentId").toString()).longValue());
                            f.setId(Double.valueOf(file.get("id").toString()).longValue());
                            f.setFileName(file.get("fileName").toString());
                            f.setUpdateTime(StringUtils.formatDate(file.get("updateTime").toString()));
                            codeList.add(f);
                          /*  if (type.equals("folder")) {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), "-", file.get("updateTime"),shareCode});
                                //table.getColumn("操作").setCellRenderer(new OperateCellRenderer());
                            } else {
                                tableModel.addRow(new Object[]{i + 1, type + ":" + file.get("fileName"), file.get("size"), file.get("updateTime"),shareCode});
                            }*/
                        }
                        tableData = new TableData<>("file", codeList, operation, fileName, sizeColumn, shareCode,updateTime);
                        table.getConfig().setShowTableTitle(false);
                        table.setTableData(tableData);

                        table.refreshDrawableState();
                        table.invalidate();
                    }
                }
            }

            @Override
            public void error(String error) {
                showToast("数据加载异常：" + error);
            }
        });

    }

    private void showToast(String message) {
        Looper.prepare(); // 初始化Looper
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (getContext() == null || getContext().getPackageName() == null) return;

                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        };
        Message msg = Message.obtain(handler, 0);
        handler.sendMessage(msg); // 发送消息到消息队列
        Looper.loop(); // 启动消息循环
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}